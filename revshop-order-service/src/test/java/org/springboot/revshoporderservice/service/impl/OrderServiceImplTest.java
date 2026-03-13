package org.springboot.revshoporderservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springboot.revshoporderservice.client.NotificationClient;
import org.springboot.revshoporderservice.client.PaymentClient;
import org.springboot.revshoporderservice.client.ProductClient;
import org.springboot.revshoporderservice.client.UserClient;
import org.springboot.revshoporderservice.dto.*;
import org.springboot.revshoporderservice.exception.BadRequestException;
import org.springboot.revshoporderservice.exception.ResourceNotFoundException;
import org.springboot.revshoporderservice.exception.UnauthorizedException;
import org.springboot.revshoporderservice.model.Order;
import org.springboot.revshoporderservice.model.OrderItem;
import org.springboot.revshoporderservice.repository.OrderRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private ProductClient productClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private CheckoutRequest checkoutRequest;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setOrderId(1L);
        testOrder.setUserId(1L);
        testOrder.setStatus("PLACED");
        
        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setQuantity(2);
        item.setPrice(50.0);
        testOrder.setOrderItems(Collections.singletonList(item));

        checkoutRequest = new CheckoutRequest();
        checkoutRequest.setShippingAddress("123 Street");
        checkoutRequest.setBillingAddress("123 Street");
        
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(2);
        itemReq.setPrice(50.0);
        checkoutRequest.setItems(Collections.singletonList(itemReq));
    }

    @Test
    void testCreateOrder_Success() {
        when(orderRepo.save(any(Order.class))).thenReturn(testOrder);
        
        PaymentResponse payResp = new PaymentResponse();
        payResp.setStatus("SUCCESS");
        when(paymentClient.makePayment(anyString(), any(PaymentRequest.class))).thenReturn(payResp);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setSellerId(101L);
        when(productClient.getProductById(anyLong(), anyString())).thenReturn(productDTO);

        Long orderId = orderService.createOrder(1L, checkoutRequest, "Bearer token");

        assertNotNull(orderId);
        assertEquals(1L, orderId);
        verify(orderRepo, atLeastOnce()).save(any(Order.class));
    }

    @Test
    void testCreateOrder_NoItems_ThrowsException() {
        checkoutRequest.setItems(null);
        assertThrows(BadRequestException.class, () -> orderService.createOrder(1L, checkoutRequest, "token"));
    }

    @Test
    void testCreateOrder_NoAddress_ThrowsException() {
        checkoutRequest.setShippingAddress(null);
        assertThrows(BadRequestException.class, () -> orderService.createOrder(1L, checkoutRequest, "token"));
    }

    @Test
    void testCreateOrder_Unauthorized_ThrowsException() {
        assertThrows(UnauthorizedException.class, () -> orderService.createOrder(null, checkoutRequest, "token"));
    }

    @Test
    void testGetOrderHistory_Success() {
        when(orderRepo.findByUserId(1L)).thenReturn(Collections.singletonList(testOrder));
        
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        when(productClient.getProductById(1L, "token")).thenReturn(productDTO);

        List<OrderHistoryResponse> result = orderService.getOrderHistory(1L, "token");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOrderId());
    }

    @Test
    void testGetOrderHistory_Empty_ThrowsException() {
        when(orderRepo.findByUserId(1L)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderHistory(1L, "token"));
    }

    @Test
    void testUpdateOrderStatus_Success() {
        when(orderRepo.findById(1L)).thenReturn(Optional.of(testOrder));

        orderService.updateOrderStatus(1L, "SHIPPED", "token");

        verify(orderRepo, times(1)).save(testOrder);
        assertEquals("SHIPPED", testOrder.getStatus());
    }
}
