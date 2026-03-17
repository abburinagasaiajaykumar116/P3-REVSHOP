package org.springboot.revshoporderservice.model;

public sealed interface OrderStatus permits 
    OrderStatus.Placed, 
    OrderStatus.Shipped, 
    OrderStatus.Delivered, 
    OrderStatus.PaymentFailed,
    OrderStatus.PaymentPending {

    record Placed() implements OrderStatus {
        @Override
        public String toString() { return "PLACED"; }
    }

    record Shipped() implements OrderStatus {
        @Override
        public String toString() { return "SHIPPED"; }
    }

    record Delivered() implements OrderStatus {
        @Override
        public String toString() { return "DELIVERED"; }
    }

    record PaymentFailed() implements OrderStatus {
        @Override
        public String toString() { return "PAYMENT_FAILED"; }
    }

    record PaymentPending() implements OrderStatus {
        @Override
        public String toString() { return "PAYMENT_PENDING"; }
    }

    static OrderStatus fromString(String status) {
        return switch (status.toUpperCase()) {
            case "PLACED" -> new Placed();
            case "SHIPPED" -> new Shipped();
            case "DELIVERED" -> new Delivered();
            case "PAYMENT_FAILED" -> new PaymentFailed();
            case "PAYMENT_PENDING" -> new PaymentPending();
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        };
    }
}
