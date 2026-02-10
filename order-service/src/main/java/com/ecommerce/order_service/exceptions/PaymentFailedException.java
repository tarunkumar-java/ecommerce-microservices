package com.ecommerce.order_service.exceptions;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String msg) { super(msg); }
}
