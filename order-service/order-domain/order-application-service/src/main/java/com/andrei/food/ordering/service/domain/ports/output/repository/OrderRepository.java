package com.andrei.food.ordering.service.domain.ports.output.repository;

import com.andrei.food.ordering.system.domain.entity.Order;
import com.andrei.food.ordering.system.domain.valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {
    Order saveOrder(Order order);

    Optional<Order> findByTrackingId(TrackingId trackingId);
}
