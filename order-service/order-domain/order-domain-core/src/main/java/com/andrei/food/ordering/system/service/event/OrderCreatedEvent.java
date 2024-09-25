package com.andrei.food.ordering.system.service.event;

import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;

import java.time.ZonedDateTime;

public class OrderCreatedEvent extends OrderEvent {


    public OrderCreatedEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }
}
