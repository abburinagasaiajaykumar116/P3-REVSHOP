package org.revshop.payment.service.Impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.revshop.payment.dto.PaymentRequest;
import org.revshop.payment.dto.PaymentResponse;
import org.revshop.payment.exception.PaymentException;
import org.revshop.payment.model.Payment;
import org.revshop.payment.repository.PaymentRepository;
import org.revshop.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    private RazorpayClient razorpayClient;

    public String createRazorpayOrder(Double amount) throws RazorpayException {

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100);
        options.put("currency", "INR");
        options.put("receipt", "order_rcptid_11");

        Order order = razorpayClient.orders.create(options);

        return order.get("id");
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {


        if(request.getAmount()<=0){
            throw new PaymentException("Invalid Payment amount");
        }

        // Try fetching the authentic Payment Method chosen by User via Razorpay API

        String authenticMethod = request.getPaymentMethod();
        if ("RAZORPAY_ONLINE".equals(authenticMethod) || authenticMethod == null) {
            try {
                if (request.getTransactionId() != null && !request.getTransactionId().isEmpty()) {
                    com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(request.getTransactionId());
                    String fetchedMethod = razorpayPayment.get("method");
                    if (fetchedMethod != null) {
                        authenticMethod = fetchedMethod.toUpperCase();
                    } else {
                        authenticMethod = "ONLINE";
                    }
                } else {
                    authenticMethod = "ONLINE";
                }
            } catch (Exception e) {
                authenticMethod = "ONLINE";
                System.out.println("Failed to fetch accurate Razorpay payment method: " + e.getMessage());
            }
        }

        Payment payment = new Payment();

        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(authenticMethod);
        payment.setPaymentStatus("SUCCESS");
        payment.setPaymentTime(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        return new PaymentResponse(saved.getPaymentId(), "SUCCESS", saved.getPaymentMethod());
    }
}