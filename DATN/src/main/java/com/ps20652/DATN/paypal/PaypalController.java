//package com.ps20652.DATN.paypal.api;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import com.paypal.api.payments.Links;
//import com.paypal.api.payments.Payment;
//import com.paypal.base.rest.PayPalRESTException;
//import com.ps20652.DATN.entity.Order;  // Đảm bảo bạn đã import đúng đối tượng Order
//
//@Controller
//public class PaypalController {
//
//    @Autowired
//    PaypalService service;
//
//    public static final String SUCCESS_URL = "pay/success";
//    public static final String CANCEL_URL = "pay/cancel";
//
//    @GetMapping("/paypal")
//    public String home() {
//        return "home";
//    }
//
//    @PostMapping("/pay")
//    public String payment(@ModelAttribute("order") Order order) {
//        try {
//            order.setIntent("sale");
//            order.setDescription("SALE");
//            order.setMethod("paypal");
//
//            // Sử dụng order trực tiếp để tạo thanh toán PayPal
//            Payment payment = service.createPayment(order, order.getMethod(),
//                order.getIntent(), order.getDescription(), "http://localhost:8080/" + CANCEL_URL,
//                "http://localhost:8080/" + SUCCESS_URL);
//
//            for (Links link : payment.getLinks()) {
//                if (link.getRel().equals("approval_url")) {
//                    return "redirect:" + link.getHref();
//                }
//            }
//        } catch (PayPalRESTException e) {
//            e.printStackTrace();
//        }
//        return "redirect:/";
//    }
//
//    @PostMapping(value = SUCCESS_URL)
//    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
//        try {
//            Payment payment = service.executePayment(paymentId, payerId);
//            System.out.println(payment.toJSON());
//            if (payment.getState().equals("approved")) {
//                return "success";
//            }
//        } catch (PayPalRESTException e) {
//            System.out.println(e.getMessage());
//        }
//        return "redirect:/";
//    }
//}

package com.ps20652.DATN.paypal;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.ps20652.DATN.entity.Account;
import com.ps20652.DATN.entity.Order; // Đảm bảo bạn đã import đúng đối tượng Order
import com.ps20652.DATN.entity.UserCart;
import com.ps20652.DATN.service.AccountService;
import com.ps20652.DATN.service.ShoppingCartService;

@Controller
public class PaypalController {

	@Autowired
	PaypalService service;

	@Autowired
    private ShoppingCartService shoppingCartService;
    
    @Autowired
    private AccountService userRepository;


	public static final String SUCCESS_URL = "pay/success";
	public static final String CANCEL_URL = "/pay/cancel";

	@GetMapping("/paypal")
	public String home( Model model, Principal principal )  {
		//lấy username 
	String username= principal.getName();
		System.out.println(username);
		int userId = getUserIDByUsername(username);
		 List<UserCart> cartItems = shoppingCartService.findByAccountUserId(userId);
            Account account = userRepository.findbyId(userId);
            int cartAmount = calculateCartAmount(cartItems);
			int cartCount = 0;
			for(UserCart item:cartItems ){
				 cartCount = cartCount + item.getQuantity();
			}
			
			model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartAmount", cartAmount);
            model.addAttribute("account", account);
		model.addAttribute("cartCount", cartCount);
		return "paypal/home";
	}

	@PostMapping("/pay")
	public String payment(@ModelAttribute("order") Order order, Principal principal) {
		try {
			String method = "paypal";
			String intent = "sale";
			String description = "SALE";
			String cancelUrl = "http://localhost:8080/" + CANCEL_URL;
			String successUrl = "http://localhost:8080/" + SUCCESS_URL;

			// order.setIntent(intent);
			order.setDescription(description);
			// order.setMethod(method);

			Payment payment = service.createPayment(order, method, intent, description, cancelUrl, successUrl,principal );

			for (Links link : payment.getLinks()) {
				if (link.getRel().equals("approval_url")) {
					System.out.println(link.getHref());
					return "redirect:" + link.getHref();
					
				}
			}
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return "redirect:/";
	}

	 @GetMapping(value = CANCEL_URL)
	    public String cancelPay() {
	        return "paypal/cancel";
	    }

	    @GetMapping(value = SUCCESS_URL)
	    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
	        try {
	            Payment payment = service.executePayment(paymentId, payerId);
	            System.out.println(payment.toJSON());
	            if (payment.getState().equals("approved")) {
	                return "paypal/success";
	            }
	        } catch (PayPalRESTException e) {
	         System.out.println(e.getMessage());
	        }
	        return "redirect:/";
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