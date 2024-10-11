package com.andrei.food.ordering.system.service.event;

import com.andrei.food.ordering.system.service.entity.Order;

import java.time.ZonedDateTime;

public class OrderPaidEvent extends OrderEvent {

    public OrderPaidEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }

}
