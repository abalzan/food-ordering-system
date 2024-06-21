package com.andrei.food.ordering.system.service.dataaccess.order.adapter;

import com.andrei.food.ordering.service.domain.ports.output.repository.OrderRepository;
import com.andrei.food.ordering.system.domain.entity.Order;
import com.andrei.food.ordering.system.domain.valueobject.TrackingId;
import com.andrei.food.ordering.system.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.andrei.food.ordering.system.service.dataaccess.order.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository, OrderDataAccessMapper orderDataAccessMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    public Order save(Order order) {
        return orderDataAccessMapper.orderEntityToOrder(
                orderJpaRepository.save(orderDataAccessMapper.orderToOrderEntity(order))
        );
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository.findByTrackingId(trackingId.getValue())
                .map(orderDataAccessMapper::orderEntityToOrder);
    }
}
