package org.revshop.payment.service.Impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.revshop.payment.dto.PaymentRequest;
import org.revshop.payment.dto.PaymentResponse;
import org.revshop.payment.exception.PaymentException;
import org.revshop.payment.model.Payment;
import org.revshop.payment.repository.PaymentRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RazorpayClient razorpayClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest paymentRequest;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(1L);
        paymentRequest.setAmount(100.0);
        paymentRequest.setPaymentMethod("CREDIT_CARD");
        paymentRequest.setTransactionId("txn_123");

        testPayment = new Payment();
        testPayment.setPaymentId(1L);
        testPayment.setOrderId(1L);
        testPayment.setAmount(100.0);
        testPayment.setPaymentMethod("CREDIT_CARD");
        testPayment.setPaymentStatus("SUCCESS");
        testPayment.setPaymentTime(LocalDateTime.now());
    }

    @Test
    void testProcessPayment_Success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        PaymentResponse response = paymentService.processPayment(paymentRequest);

        assertNotNull(response);
        assertEquals(1L, response.getPaymentId());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("CREDIT_CARD", response.getPaymentMethod());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_InvalidAmount_ThrowsException() {
        paymentRequest.setAmount(-50.0);
        assertThrows(PaymentException.class, () -> paymentService.processPayment(paymentRequest));
    }

}
