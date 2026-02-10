package com.ecommerce.order_service.exceptions;

public class DuplicateOrderException extends RuntimeException{
    public DuplicateOrderException(String msg) { super(msg); }
}
