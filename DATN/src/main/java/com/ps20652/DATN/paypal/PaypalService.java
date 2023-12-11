////
////package com.ps20652.DATN.paypal.api;
////
////import java.math.BigDecimal;
////import java.math.RoundingMode;
////import java.util.ArrayList;
////import java.util.List;
////
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Service;
////
////import com.paypal.api.payments.Amount;
////import com.paypal.api.payments.Payer;
////import com.paypal.api.payments.Payment;
////import com.paypal.api.payments.PaymentExecution;
////import com.paypal.api.payments.RedirectUrls;
////import com.paypal.api.payments.Transaction;
////import com.paypal.base.rest.APIContext;
////import com.paypal.base.rest.PayPalRESTException;
////
////@Service
////public class PaypalService {
////
////	@Autowired
////	private APIContext apiContext;
////
////	public Payment createPayment(Double total, String currency, String method, String intent, String description,
////			String cancelUrl, String successUrl) throws PayPalRESTException {
////		Amount amount = new Amount();
////		amount.setCurrency(currency);
////		total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
////		amount.setTotal(String.format("%.2f", total));
////
////		Transaction transaction = new Transaction();
////		transaction.setDescription(description);
////		transaction.setAmount(amount);
////
////		List<Transaction> transactions = new ArrayList<>();
////		transactions.add(transaction);
////
////		Payer payer = new Payer();
////		payer.setPaymentMethod(method.toString());
////
////		Payment payment = new Payment();
////		payment.setIntent(intent.toString());
////		payment.setPayer(payer);
////		payment.setTransactions(transactions);
////		RedirectUrls redirectUrls = new RedirectUrls();
////		redirectUrls.setCancelUrl(cancelUrl);
////		redirectUrls.setReturnUrl(successUrl);
////		payment.setRedirectUrls(redirectUrls);
////
////		return payment.create(apiContext);
////	}
////
////	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
////		Payment payment = new Payment();
////		payment.setId(paymentId);
////		PaymentExecution paymentExecute = new PaymentExecution();
////		paymentExecute.setPayerId(payerId);
////		return payment.execute(apiContext, paymentExecute);
////	}
////
////}
//
//package com.ps20652.DATN.paypal.api;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.paypal.api.payments.Amount;
//import com.paypal.api.payments.Payer;
//import com.paypal.api.payments.Payment;
//import com.paypal.api.payments.PaymentExecution;
//import com.paypal.api.payments.RedirectUrls;
//import com.paypal.api.payments.Transaction;
//import com.paypal.base.rest.APIContext;
//import com.paypal.base.rest.PayPalRESTException;
//import com.ps20652.DATN.DAO.OrderDAO;
//import com.ps20652.DATN.entity.Order; // Import đối tượng Order
//
//@Service
//public class PaypalService {
//
//    @Autowired
//    private APIContext apiContext;
//
//    public Payment createPayment(Order order, String method, String description, String intent, String cancelUrl, String successUrl) throws PayPalRESTException {
//        Amount amount = new Amount();
//        amount.setCurrency("USD"); // Đổi thành mã tiền tệ của bạn nếu cần
////        BigDecimal total = order.getTotalPrice();
//        BigDecimal total = new BigDecimal(10);
//        total = total.setScale(2, RoundingMode.HALF_UP);
//        amount.setTotal(String.format("%.2f", total));
//
//        Transaction transaction = new Transaction();
//        transaction.setDescription(description);
//        transaction.setAmount(amount);
//
//        List<Transaction> transactions = new ArrayList<>();
//        transactions.add(transaction);
//
//        Payer payer = new Payer();
//        payer.setPaymentMethod(method);
//
//        Payment payment = new Payment();
//        payment.setIntent(intent);
//        payment.setPayer(payer);
//        payment.setTransactions(transactions);
//
//        RedirectUrls redirectUrls = new RedirectUrls();
//        redirectUrls.setCancelUrl(cancelUrl);
//        redirectUrls.setReturnUrl(successUrl);
//        payment.setRedirectUrls(redirectUrls);
//
//        return payment.create(apiContext);
//    }
//
//    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
//        Payment payment = new Payment();
//        payment.setId(paymentId);
//        PaymentExecution paymentExecute = new PaymentExecution();
//        paymentExecute.setPayerId(payerId);
//        return payment.execute(apiContext, paymentExecute);
//    }
//}
//

package com.ps20652.DATN.paypal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.ps20652.DATN.entity.Account;
import com.ps20652.DATN.entity.Order; // Đảm bảo bạn đã import đúng đối tượng Order
import com.ps20652.DATN.entity.UserCart;
import com.ps20652.DATN.service.AccountService;
import com.ps20652.DATN.service.ShoppingCartService;

@Service
public class PaypalService {

	@Autowired
	private APIContext apiContext;

		@Autowired
    private ShoppingCartService shoppingCartService;
    
    @Autowired
    private AccountService userRepository;

	public Payment createPayment(Order order, String method, String intent, String description, String cancelUrl,
	        String successUrl, Principal principal) throws PayPalRESTException {
				String username= principal.getName();
		System.out.println(username);
		int userId = getUserIDByUsername(username);
		 List<UserCart> cartItems = shoppingCartService.findByAccountUserId(userId);
            Account account = userRepository.findbyId(userId);
            double cartAmount = calculateCartAmount(cartItems);
	    Amount amount = new Amount();
		
	    order.setTotalPrice(cartAmount);
	    amount.setCurrency("USD"); // Change to your desired currency code
	    Double total = order.getTotalPrice().doubleValue(); // Convert BigDecimal to Double
	    amount.setTotal(String.format("%.2f", cartAmount));

	    Transaction transaction = new Transaction();
	    transaction.setDescription(description);
	    transaction.setAmount(amount);

	    List<Transaction> transactions = new ArrayList<>();
	    transactions.add(transaction);

	    Payer payer = new Payer();
	    payer.setPaymentMethod(method);

	    Payment payment = new Payment();
	    payment.setIntent(intent);
	    payment.setPayer(payer);
	    payment.setTransactions(transactions);

	    RedirectUrls redirectUrls = new RedirectUrls();
	    redirectUrls.setCancelUrl(cancelUrl);
	    redirectUrls.setReturnUrl(successUrl);
	    payment.setRedirectUrls(redirectUrls);

	    return payment.create(apiContext);
	}


	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		return payment.execute(apiContext, paymentExecute);
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
