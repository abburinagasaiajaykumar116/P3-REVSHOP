package org.revshop.payment.service;

import org.revshop.payment.dto.PaymentRequest;
import org.revshop.payment.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);
    String createRazorpayOrder(Double amount) throws com.razorpay.RazorpayException;
}