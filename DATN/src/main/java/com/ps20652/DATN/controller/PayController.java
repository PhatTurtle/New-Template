package com.ps20652.DATN.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ps20652.DATN.entity.Account;
import com.ps20652.DATN.entity.Order;
import com.ps20652.DATN.entity.OrderDetail;
import com.ps20652.DATN.entity.Product;
import com.ps20652.DATN.entity.Revenue;
import com.ps20652.DATN.entity.UserCart;
import com.ps20652.DATN.entity.Voucher;
import com.ps20652.DATN.service.AccountService;
import com.ps20652.DATN.service.OrderDetailService;
import com.ps20652.DATN.service.OrderService;
import com.ps20652.DATN.service.ProductService;
import com.ps20652.DATN.service.RevenueService;
import com.ps20652.DATN.service.ShoppingCartService;
import com.ps20652.DATN.service.VoucherService;

@Controller
@RequestMapping("/pay")
public class PayController {

	@Autowired
    private ShoppingCartService shoppingCartService;
    
    @Autowired
    private AccountService userRepository;
    
    @Autowired
    private OrderService orderService;
  
    @Autowired
    private OrderDetailService orderDetialService;
    
    @Autowired
    private RevenueService revenueService;
    
    @Autowired
    private VoucherService voucherService;
	@Autowired
    private ProductService productService;
	
    @GetMapping
    public String pay(Principal principal, Model model, @RequestParam(value = "selectedVoucherId", required = false) Integer selectedVoucherId, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            String username = principal.getName();
            int userId = getUserIDByUsername(username);
            
            model.addAttribute("username", username);
             
           
            int cartItemCount = shoppingCartService.getCount(userId);
            model.addAttribute("cartItemCount", cartItemCount);
            
            if(cartItemCount == 0){
                redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng của bạn đang trống");
               return "redirect:/cart";
            }
            List<UserCart> cartItems = shoppingCartService.findByAccountUserId(userId);
            Account account = userRepository.findbyId(userId);
            int cartAmount = calculateCartAmount(cartItems);

            // Nếu mã giảm giá được chọn, thực hiện xử lý tương ứng
            if (selectedVoucherId != null) {
                // Lấy thông tin về mã giảm giá từ ID và thực hiện logic của bạn ở đây.
                // Ví dụ:
                Voucher selectedVoucher = voucherService.findbyId(selectedVoucherId);
                int discountAmount = selectedVoucher.getDiscountAmount();
                int id = selectedVoucher.getVoucherId();
                // Truyền thông tin về mã giảm giá đã chọn đến view
                model.addAttribute("selectedVoucher", selectedVoucher);
                model.addAttribute("voucherId", id);
                model.addAttribute("discountAmount", discountAmount);
            }

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartAmount", cartAmount);
            model.addAttribute("account", account);

            // Lấy danh sách các mã voucher của người dùng
            Account user = userRepository.findbyId(userId);
            model.addAttribute("user", user);
        }

        return "app/layout/pay";
    }
    static List<UserCart> arr = new ArrayList<>();
    @GetMapping("/{id}")
    public String bynow(@PathVariable("id") Integer id,Model model, Principal principal,@RequestParam(value = "selectedVoucherId", required = false) Integer selectedVoucherId){
        
        if (principal != null) {
            String username = principal.getName();
            int userId = getUserIDByUsername(username);
            Account account = userRepository.findbyId(userId);
            
            Product product = productService.findbyId(id);
            UserCart items = new UserCart(1,account, product, 1);
            // List<UserCart> cartItems = Arrays.asList(items);

            // arr = Arrays.asList(items);

            List<UserCart> data = Arrays.asList(items);
            arr = new ArrayList<>(data);
            Account user = userRepository.findbyId(userId);
              int cartAmount = calculateCartAmount(arr);
            if (selectedVoucherId != null) {
                // Lấy thông tin về mã giảm giá từ ID và thực hiện logic của bạn ở đây.
                // Ví dụ:
                Voucher selectedVoucher = voucherService.findbyId(selectedVoucherId);
                int discountAmount = selectedVoucher.getDiscountAmount();
                int voucherid = selectedVoucher.getVoucherId();
                // Truyền thông tin về mã giảm giá đã chọn đến view
                model.addAttribute("selectedVoucher", selectedVoucher);
                model.addAttribute("voucherId", voucherid);
                model.addAttribute("discountAmount", discountAmount);
            }
            model.addAttribute("user", user);
            model.addAttribute("username", username);
             model.addAttribute("cartItems", arr);
             model.addAttribute("cartAmount", cartAmount);
             model.addAttribute("account", account);
        }
        return "app/layout/pay";
    }
	
	
	@GetMapping("/checkout")
    public String processCheckout(RedirectAttributes redirectAttributes,  @RequestParam("selectedVouchers") Integer selectedVoucherId, @RequestParam("userId") Integer userId, Model model, @RequestParam("fullname") String fullname, @RequestParam("phone") Integer phone, @RequestParam("address") String address, @RequestParam("description") String description) {
        try {
            List<UserCart> userCart = shoppingCartService.findByAccountUserId(userId);

            if (userCart.isEmpty() && arr == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng của bạn trống. Không thanh toán được");
                return "redirect:/cart"; // Trả về trang lỗi hoặc trang thông báo
            }
            Account user = userRepository.findbyId(userId);

            // Tạo đơn hàng mới
            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(new Date());
            order.setStatus("Chờ xác nhận");
            order.setFullName(fullname);
            order.setPhone(phone);
            order.setAddress(address);
            order.setDescription(description);

            Voucher voucher = null;
            double discountAmount = 0;

            if (selectedVoucherId > 0) {
                // Nếu selectedVoucherId khác 0, lấy thông tin voucher từ ID
                voucher = voucherService.findbyId(selectedVoucherId);
                if (voucher != null) {
                    int remainingQuantity = voucher.getQuantity() - 1;
                    if (remainingQuantity >= 0) {
                        System.out.println("test");
                        voucher.setQuantity(remainingQuantity);
                        voucherService.createVoucher(voucher); // Cập nhật số lượng mã giảm giá
                        discountAmount = voucher.getDiscountAmount(); // Lấy giá trị giảm giá từ voucher
                    } else {
                    	 
                         redirectAttributes.addFlashAttribute("errorMessage", "Số lượng mã giảm giá đã hết");
                        return "redirect:/cart";
                    }
                }
            }

            double totalAmount = 0.0;
            List<OrderDetail> orderDetails = new ArrayList<>();
            System.out.println("voucher"+ selectedVoucherId);
            
            if( arr == null || arr.size() == 0){
            
                for (UserCart cartItem : userCart) {
                Product product = cartItem.getProduct();
                totalAmount += product.getPrice() * cartItem.getQuantity();

              //  Trừ giảm giá nếu có voucher và selectedVoucherId khác 0
                    if (selectedVoucherId != 0) {
                        totalAmount -= discountAmount;
                    }
                    
                    if (product.getQuantityInStock() >= cartItem.getQuantity()) {
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setOrder(order);
                        orderDetail.setProduct(product);
                        orderDetail.setQuantity(cartItem.getQuantity());
                        orderDetail.setPrice(product.getPrice());
                        orderDetails.add(orderDetail);

                        // Giảm số lượng trong kho sau khi đặt hàng
                        product.setQuantityInStock(product.getQuantityInStock() - cartItem.getQuantity());
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm " + product.getName() + " không có đủ hàng trong kho.");
                        return "redirect:/cart"; // Trả về trang lỗi hoặc trang thông báo
                    }
                }
                order.setTotalPrice(totalAmount);
                order.setVoucher(voucher);
                orderService.create(order); 
                orderDetialService.create(orderDetails);
                
                // Thêm vào bảng Doanh Thu
                Revenue revenue = new Revenue();
                revenue.setOrder(order);
                revenue.setCustomer(user);
                revenue.setOrderDate(new Date());
                revenue.setTotalAmount(totalAmount);
                revenue.setPaymentMethod("Cash"); // Hoặc phương thức thanh toán khác nếu có
                
                // Lưu thông tin doanh thu vào cơ sở dữ liệu
                revenueService.create(revenue);
            }else{
                for (UserCart cartItem : arr) {
                    Product product = cartItem.getProduct();
                    totalAmount += product.getPrice() * cartItem.getQuantity();

                //  Trừ giảm giá nếu có voucher và selectedVoucherId khác 0
                    if (selectedVoucherId != 0) {
                        totalAmount -= discountAmount;
                    }
                    
                    if (product.getQuantityInStock() >= cartItem.getQuantity()) {
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setOrder(order);
                        orderDetail.setProduct(product);
                        orderDetail.setQuantity(cartItem.getQuantity());
                        orderDetail.setPrice(product.getPrice());
                        orderDetails.add(orderDetail);

                        // Giảm số lượng trong kho sau khi đặt hàng
                        System.out.println(product.getQuantityInStock());
                        product.setQuantityInStock(product.getQuantityInStock() - cartItem.getQuantity());
                        
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm " + product.getName() + " không có đủ hàng trong kho.");
                        return "redirect:/cart"; // Trả về trang lỗi hoặc trang thông báo
                    }
                }

            order.setTotalPrice(totalAmount);
            order.setVoucher(voucher);

            orderService.create(order); 
            orderDetialService.create(orderDetails);
            
            // Thêm vào bảng Doanh Thu
            Revenue revenue = new Revenue();
            revenue.setOrder(order);
            revenue.setCustomer(user); 
            revenue.setOrderDate(new Date());
            revenue.setTotalAmount(totalAmount);
            revenue.setPaymentMethod("Cash"); // Hoặc phương thức thanh toán khác nếu có
            
            // Lưu thông tin doanh thu vào cơ sở dữ liệu
            revenueService.create(revenue);
            }
            

          
            
            System.out.println("helllo");
            // Trả về trang xác nhận đặt hàng hoặc trang thành công
        
        if(arr.size() == 0 ){
            shoppingCartService.clearUserCart(userId);
                
            }else{
                clearGlobalArrayList(arr);
            }

            redirectAttributes.addFlashAttribute("confirmationMessage", "Đặt hàng thành công");
            return "redirect:/orders"; // Trả về trang xác nhận đặt hàng hoặc trang thành công
        } catch (Exception e) {
            e.printStackTrace();
        	redirectAttributes.addFlashAttribute("errorMessage", "Lỗi trong quá trình đặt hàng");
            return "redirect:/cart"; // Trả về trang lỗi hoặc trang thông báo
        }
    }
    
	static void clearGlobalArrayList(List<UserCart> arr) {
        System.out.println(arr.size());
        arr.remove(0);
        System.out.println(arr.size());
    }
	
	private int getUserIDByUsername(String username) {
        // Sử dụng Spring Data JPA để truy vấn cơ sở dữ liệu
        Account user = userRepository.findByUsername(username);

        if (user != null) {
            return user.getUserId(); // Trả về userID từ đối tượng User
        }

        return -1; // Trường hợp không tìm thấy user
    }
    private int calculateCartAmount(List<UserCart> cartItems) {
        int totalAmount = 0;
        for (UserCart item : cartItems) {
            totalAmount += item.getProduct().getPrice() * item.getQuantity();
        }
        return totalAmount;
    }
	
	
}
