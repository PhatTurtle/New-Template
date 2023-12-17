package com.ps20652.DATN.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ps20652.DATN.entity.Account;
import com.ps20652.DATN.service.AccountService;
import com.ps20652.DATN.service.ShoppingCartService;


@Controller
public class NotFoundController implements ErrorController {
 
    @Autowired
    ShoppingCartService cartService;
    @Autowired
    AccountService accountService;
     @RequestMapping("/error")
        public String handleError(HttpServletRequest request, Authentication authentication, Model model) {
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                model.addAttribute("username", username);
    
                // Lấy userID của người dùng đã đăng nhập
                int userId = getUserIDByUsername(username);
    
                // Lấy số lượng sản phẩm trong giỏ hàng
                int cartItemCount = cartService.getCount(userId);
                model.addAttribute("cartItemCount", cartItemCount);
            }
            Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "/security/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "/security/404";
            }
            }
            return "/security/404";
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
