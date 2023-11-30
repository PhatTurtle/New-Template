package com.ps20652.DATN.controller;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ps20652.DATN.entity.Account;
import com.ps20652.DATN.entity.Category;
import com.ps20652.DATN.entity.CustomerFeedback;
import com.ps20652.DATN.entity.OrderDetail;
import com.ps20652.DATN.entity.Product;

import com.ps20652.DATN.entity.ReviewReply;
import com.ps20652.DATN.service.AccountService;
import com.ps20652.DATN.service.CategoryService;
import com.ps20652.DATN.service.FeedbackService;
import com.ps20652.DATN.service.OrderDetailService;
import com.ps20652.DATN.service.ProductService;
import com.ps20652.DATN.service.ReviewReplyService;
import com.ps20652.DATN.service.ShoppingCartService;
import com.ps20652.DATN.service.UploadService;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OrderDetailService orderDetailService;
    // @Autowired
    // private ReviewReplyService replyService;
    //

    @GetMapping("/listproduct")
    public String product(Model model, Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        List<Product> products = productService.findAll();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        }

        int pageSize = 12; // Số lượng đơn hàng trên mỗi trang
        Page<Product> productPage = productService.getAllOrdersPaginated(PageRequest.of(page, pageSize));
        List<Category> cat = categoryService.findAll();
        model.addAttribute("allcategory", cat);
        model.addAttribute("products", productPage);
        model.addAttribute("check", "check2");
        Map<Integer, Integer> unitsSoldMap = new HashMap<>();
        for (Product product : products) {
            int unitsSold = orderDetailService.getProductSell(product);
            unitsSoldMap.put(product.getProductId(), unitsSold);
        }
        model.addAttribute("unitsSoldMap", unitsSoldMap);
        return "app/layout/list_product";
    }

    @GetMapping
    public String listProducts(Model model, Authentication authentication,
            @RequestParam(name = "confirmationMessage", required = false) String confirmationMessage,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        List<CustomerFeedback> feedback = feedbackService.findAll();
        List<Product> products = productService.findAll();
        List<Category> cat = categoryService.findAll();
        Map<Integer, Integer> unitsSoldMap = new HashMap<>();
        Map<Integer, List<Product>> topProductsPerCategory = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);

            // Lấy userID của người dùng đã đăng nhập
            int userId = getUserIDByUsername(username);

            // Lấy số lượng sản phẩm trong giỏ hàng
            int cartItemCount = cartService.getCount(userId);
            model.addAttribute("cartItemCount", cartItemCount);
        }

        for (Product product : products) {
            int unitsSold = orderDetailService.getProductSell(product);
            unitsSoldMap.put(product.getProductId(), unitsSold);
        }

        // for (Category category : cat) {
        // List<Product> topProducts =
        // productService.getTop4BestSellingProductsPerCategory(category.getCategoryId());
        // topProductsPerCategory.put((category.getCategoryId()), topProducts);
        // }

        if (confirmationMessage != null) {
            model.addAttribute("confirmationMessage", confirmationMessage);
        }

        List<Product> top8Product = productService.getEightProducts();

        List<Product> top4Product = productService.getTop4BestSellingProducts();

        model.addAttribute("topProductsPerCategory", topProductsPerCategory);
        model.addAttribute("unitsSoldMap", unitsSoldMap);
        model.addAttribute("products", products);

        model.addAttribute("allcategory", cat);
        model.addAttribute("fb", feedback);
        model.addAttribute("top8Product", top8Product);
        model.addAttribute("top4Product", top4Product);

        return "components/index";
    }

    @GetMapping("/login")
    public String login() {
        return "app/auth/login/sign_in";
    }

    @GetMapping("/new")
    public String New(Principal principal, Model model) {

        if (principal != null) {
            String username = principal.getName();
            int userId = getUserIDByUsername(username);

            model.addAttribute("username", username);

            int cartItemCount = cartService.getCount(userId);
            model.addAttribute("cartItemCount", cartItemCount);

        }

        List<Category> cat = categoryService.findAll();
        model.addAttribute("allcategory", cat);

        return "app/layout/blogs";
    }

    @GetMapping("/contact")
    public String contact(Principal principal, Model model) {

        if (principal != null) {
            String username = principal.getName();
            int userId = getUserIDByUsername(username);

            model.addAttribute("username", username);

            int cartItemCount = cartService.getCount(userId);
            model.addAttribute("cartItemCount", cartItemCount);

        }
        List<Category> cat = categoryService.findAll();
        model.addAttribute("allcategory", cat);
        return "app/layout/contact";
    }

    @GetMapping("/about")
    public String About(Principal principal, Model model) {

        if (principal != null) {
            String username = principal.getName();
            int userId = getUserIDByUsername(username);

            model.addAttribute("username", username);

            int cartItemCount = cartService.getCount(userId);
            model.addAttribute("cartItemCount", cartItemCount);

            List<Category> cat = categoryService.findAll();
            model.addAttribute("allcategory", cat);
        }

        return "app/layout/about";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "redirect:/"; // Trang thông báo lỗi truy cập không được phép
    }

    @GetMapping("/ProductDetails/{productId}")
    public String IDProducts(@PathVariable("productId") Integer productId, Model model, Principal principal) {
        Product product = productService.findbyId(productId);
        List<CustomerFeedback> feedback = feedbackService.findByProductProductId(productId);

        if (principal != null) {
            if (principal != null) {
                String username = principal.getName();
                int userId = getUserIDByUsername(username);

                model.addAttribute("username", username);
                model.addAttribute("userId", userId);
                int cartItemCount = cartService.getCount(userId);
                model.addAttribute("cartItemCount", cartItemCount);

            }
        }

        model.addAttribute("products", product);
        model.addAttribute("feedbacks", feedback);

        return "app/layout/product_detail";
    }

    // @GetMapping ("/ProductDetails/{productId}")
    // public String IDProducts(@PathVariable("productId") Integer productId,Model
    // model, @RequestParam Integer feedbackId ) {
    // Product product = productService.findbyId(productId);
    // List <CustomerFeedback> feedback =
    // feedbackService.findByProductProductId(productId);
    // List <ReviewReply> rep = replyService.findByFeedbackId(feedbackId);
    // model.addAttribute("products", product);
    // model.addAttribute("reply", rep);
    // model.addAttribute("feedbacks", feedback);
    // return "product-details";
    // }

    @GetMapping("/searchName")
    public String searchProductsNAME(@RequestParam("name") String productName, Model model, Authentication authentication,  @RequestParam(name = "page", defaultValue = "0") int page) {
        // Sử dụng phương thức tìm kiếm theo tên từ ProductDAO
        // Đây là ví dụ tìm kiếm theo tên sản phẩm
        // List<Product> searchResults = productService.findByName(productName);
        int pageSize = 12; // Số lượng đơn hàng trên mỗi trang
        Page<Product> searchResults = productService.findByNamePaginated(productName, PageRequest.of(page, pageSize));
        // List<Product> products = productService.findAll();
         Map<Integer, Integer> unitsSoldMap = new HashMap<>();

         if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);

            // Lấy userID của người dùng đã đăng nhập
            int userId = getUserIDByUsername(username);

            // Lấy số lượng sản phẩm trong giỏ hàng
            int cartItemCount = cartService.getCount(userId);
            model.addAttribute("cartItemCount", cartItemCount);
        }
        for (Product product : searchResults.getContent()) {
            int unitsSold = orderDetailService.getProductSell(product);
            unitsSoldMap.put(product.getProductId(), unitsSold);
        }
        // for (Product product : searchResults) {
        //     int unitsSold = orderDetailService.getProductSell(product);
        //     unitsSoldMap.put(product.getProductId(), unitsSold);
        // }
        
        
        model.addAttribute("unitsSoldMap", unitsSoldMap);
        model.addAttribute("products", searchResults);
        

        return "app/layout/list_product"; // Trả về view để hiển thị kết quả tìm kiếm
    }

    @GetMapping("/searchPrice")
    public String price(HttpServletRequest request, Model model, @RequestParam("min") double min,
            @RequestParam("max") double max) {
        // List<Product> items = dao.findByPriceBetween(min, max);

        List<Product> searchResults = productService.findByPrice(min, max);
        model.addAttribute("products", searchResults);

        return "user2/index2"; // Trả về view để hiển thị kết quả lọc
    }

    @GetMapping("/product/category/{categoryId}")
    public String productCategory(@RequestParam(value = "size", defaultValue = "8") int size,
            @PathVariable("categoryId") Integer categoryId, Model model, Principal principal,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        List<Category> allcat = categoryService.findAll();
        Category cat = categoryService.findbyId(categoryId);
        List<Product> products = productService.findByCategoryCategoryId(categoryId);
        if (principal != null) {
            String username = principal.getName();
            int userId = getUserIDByUsername(username);

            model.addAttribute("username", username);

            int cartItemCount = cartService.getCount(userId);
            model.addAttribute("cartItemCount", cartItemCount);

        }

        Page<Product> productPage = productService.getAllProductCategory(categoryId, page, size);
        model.addAttribute("check", "check");
        model.addAttribute("categories", cat);
        model.addAttribute("products", productPage);
        model.addAttribute("allcategory", allcat);
        model.addAttribute("categoryID", categoryId);
        Map<Integer, Integer> unitsSoldMap = new HashMap<>();
        for (Product product : products) {
            int unitsSold = orderDetailService.getProductSell(product);
            unitsSoldMap.put(product.getProductId(), unitsSold);
        }
        model.addAttribute("unitsSoldMap", unitsSoldMap);
        return "app/layout/list_product";
    }

    private int getUserIDByUsername(String username) {
        // Sử dụng Spring Data JPA để truy vấn cơ sở dữ liệu
        Account user = accountService.findByUsername(username);

        if (user != null) {
            return user.getUserId(); // Trả về userID từ đối tượng User
        }

        return -1; // Trường hợp không tìm thấy user
    }

}
