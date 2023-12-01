package com.ps20652.DATN.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ps20652.DATN.entity.Account;
import com.ps20652.DATN.service.AccountService;
import com.ps20652.DATN.service.OTPService;

@Controller
@RequestMapping("/")
public class RegistrationController {

	@Autowired
	private AccountService userService;

	@GetMapping("/register")
	public String showRegistrationForm(Model model, RedirectAttributes redirectAttributes) {
		model.addAttribute("user", new Account());
		model.addAttribute("registrationError", ""); // Đảm bảo trường lỗi được truyền vào giao diện

		redirectAttributes.addFlashAttribute("confirmationMessage", "Đăng ký tài khoản thành công");
		return "app/auth/login/sign-up";
	}

	@GetMapping("/checkUsername")
	@ResponseBody
	public String checkUsernameExists(@RequestParam("username") String username) {
		boolean exists = userService.existsByUsername(username);
		return String.valueOf(exists);
	}

	@Autowired
	private OTPService otpService;

	// @PostMapping("/register")
	// public String registerUser(@ModelAttribute("user") Account user, Model model,
	// @RequestParam("email") String email,
	// @RequestParam("username") String username, @RequestParam("password") String
	// password, HttpSession session) {
	// Kiểm tra username đã tồn tại

	//
	// // Kiểm tra email đã tồn tại

	//
	// // Kiểm tra độ dài password

	//
	// // Tiếp tục xử lý nếu không có lỗi
	// // ...
	//
	// // Chuyển hướng đến trang xác thực OTP
	// return "redirect:/verifyOTP?email=" + email;
	// }
	@PostMapping("/register")
	public String registerUser(@ModelAttribute("user") Account user, Model model, @RequestParam("email") String email,
			@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {
		user.setUsername(username);
		user.setPassword(password);
		// ... (tạo OTP, gửi email)

		if (userService.existsByUsername(username)) {
			model.addAttribute("registrationError", "Username đã tồn tại!");
			return "app/auth/login/sign-up";
		}
		if (userService.existsByEmail(email)) {
			model.addAttribute("registrationError", "Email đã được đăng ký!");
			return "app/auth/login/sign-up";
		}

		if (password.length() < 8) {
			model.addAttribute("registrationError", "Password phải có ít nhất 8 ký tự!");
			return "app/auth/login/sign-up";
		}

		String otp = otpService.generateOTP();
		otpService.saveOTP(email, otp);
		// Kiểm tra acc để đảm bảo không null trước khi sử dụng
		Account acc = otpService.sendOTP(email);
		if (acc != null) {
			user.setEmail(acc.getEmail());
			user.setOtp(acc.getOtp());
			user.setOtpCreatedAt(acc.getOtpCreatedAt());
		} else {

			// Xử lý trường hợp acc là null ở đây nếu cần thiết
		}
		

		// userService.create(user);

		// Lưu thông tin người dùng đã đăng ký vào session
		session.setAttribute("registeredUser", user);

		

		

		// Chuyển hướng đến trang xác thực OTP
		return "redirect:/verifyOTP?email=" + email; // Sửa user.getEmail() thành email
	}

	@PostMapping("/verifyOTP")
public String verifyOTP(@RequestParam("email") String email, @RequestParam("otp") String otp, Model model,
        HttpSession session, RedirectAttributes redirectAttributes) {
    if (otpService.verifyOTP(email, otp)) {
        Account registeredUser = (Account) session.getAttribute("registeredUser");
        if (registeredUser != null) {
            // Xác thực thành công, lưu thông tin người dùng vào session
            // và chỉ tạo tài khoản khi OTP nhập đúng
            userService.create(registeredUser);

            session.setAttribute("user", registeredUser);
            redirectAttributes.addFlashAttribute("confirmationMessage", "Đăng ký tài khoản thành công");
            return "redirect:/login"; // Chuyển hướng đến trang đăng nhập
        }
    }
    // Xác thực không thành công, thông báo lỗi
    model.addAttribute("error", "Mã OTP không hợp lệ hoặc đã hết hạn.");
    return "app/auth/login/verifyOtp"; // Trở lại trang xác thực OTP
}


	@GetMapping("/verifyOTP")
	public String showVerifyOTPPage(@RequestParam("email") String email, Model model) {
		model.addAttribute("email", email);
		return "app/auth/login/verifyOtp"; // Trả về trang xác thực OTP
	}

}