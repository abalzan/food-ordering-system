package com.andrei.food.ordering.system.domain;

import com.andrei.food.ordering.system.domain.entity.Order;
import com.andrei.food.ordering.system.domain.entity.Restaurant;
import com.andrei.food.ordering.system.domain.event.OrderCanceledEvent;
import com.andrei.food.ordering.system.domain.event.OrderCreatedEvent;
import com.andrei.food.ordering.system.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant);

    OrderPaidEvent payOder(Order order);

    void ApproveOrder(Order order);

    OrderCanceledEvent cancelOrderPayment(Order order, List<String> failureMessages);

    void cancelOrder(Order order, List<String> failureMessages);
}
