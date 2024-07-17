package com.andrei.food.ordering.system.service;

import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.entity.Restaurant;
import com.andrei.food.ordering.system.service.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.service.event.OrderCreatedEvent;
import com.andrei.food.ordering.system.service.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant);

    OrderPaidEvent payOder(Order order);

    void ApproveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages);

    void cancelOrder(Order order, List<String> failureMessages);
}
