package com.andrei.food.ordering.system.service.dataaccess.order.adapter;

import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.valueobject.TrackingId;
import com.andrei.food.ordering.system.service.dataaccess.order.entity.OrderEntity;
import com.andrei.food.ordering.system.service.dataaccess.order.repository.OrderJpaRepository;
import com.andrei.food.ordering.system.service.dataaccess.order.mapper.OrderDataAccessMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class OrderRepositoryImplTest {

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @Mock
    private OrderDataAccessMapper orderDataAccessMapper;

    private OrderRepositoryImpl orderRepositoryImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderRepositoryImpl = new OrderRepositoryImpl(orderJpaRepository, orderDataAccessMapper);
    }

    @Test
    void shouldSaveOrder() {
        Order order = Order.builder().build();
        when(orderDataAccessMapper.orderToOrderEntity(order)).thenReturn(new OrderEntity());
        when(orderJpaRepository.save(new OrderEntity())).thenReturn(new OrderEntity());
        when(orderDataAccessMapper.orderEntityToOrder(new OrderEntity())).thenReturn(order);

        Order savedOrder = orderRepositoryImpl.save(order);

        assertEquals(order, savedOrder);
    }

    @Test
    void shouldFindByTrackingId() {
        TrackingId trackingId = new TrackingId(UUID.randomUUID());
        Order order = Order.builder().build();
        when(orderJpaRepository.findByTrackingId(trackingId.getValue())).thenReturn(Optional.of(new OrderEntity()));
        when(orderDataAccessMapper.orderEntityToOrder(new OrderEntity())).thenReturn(order);

        Optional<Order> foundOrder = orderRepositoryImpl.findByTrackingId(trackingId);

        assertEquals(Optional.of(order), foundOrder);
    }
}
