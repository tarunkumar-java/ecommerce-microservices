package com.ecommerce.order_service.utility;

import com.ecommerce.order_service.enums.OrderEnum;
import org.aspectj.weaver.ast.Or;

import java.util.concurrent.atomic.AtomicInteger;

public class OrderUtility {

    public static OrderEnum getOrderStatus(String orderStatus) {
        return switch (orderStatus) {
            case "Created" -> OrderEnum.CREATED;
            case "Canceled" ->OrderEnum.CANCELLED;
            case "Confirm"    -> OrderEnum.CONFIRMED;
            default        -> throw new IllegalArgumentException("Invalid order status: " + orderStatus);
        };
    }

}