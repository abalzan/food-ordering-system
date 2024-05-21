package com.andrei.food.ordering.system.domain.event;

import com.andrei.food.ordering.system.domain.domain.events.DomainEvent;
import com.andrei.food.ordering.system.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderPaidEvent extends OrderEvent {

    public OrderPaidEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }
}