package com.andrei.food.ordering.system.service.domain.ports.output.repository;

import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findByTrackingId(TrackingId trackingId);
}
