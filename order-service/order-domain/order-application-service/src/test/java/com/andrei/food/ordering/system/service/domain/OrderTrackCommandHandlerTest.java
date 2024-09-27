package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.service.domain.dto.track.TrackOrderQuery;
import com.andrei.food.ordering.system.service.domain.dto.track.TrackOrderResponse;
import com.andrei.food.ordering.system.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.system.service.domain.ports.output.repository.OrderRepository;
import com.andrei.food.ordering.system.service.exception.OrderNotFoundException;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.service.valueobject.OrderStatus;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.valueobject.TrackingId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderTrackCommandHandlerTest {

    @Mock
    private OrderDataMapper orderDataMapper;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTrackCommandHandler orderTrackCommandHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void trackOrderReturnsResponseWhenOrderFound() {
        UUID orderTrackingId = UUID.randomUUID();
        Order order = Order.builder().orderId(new OrderId(UUID.randomUUID())).trackingId(new TrackingId(orderTrackingId)).orderStatus(OrderStatus.APPROVED).build();
        TrackOrderQuery query = new TrackOrderQuery(orderTrackingId);
        TrackOrderResponse response = new TrackOrderResponse(orderTrackingId, OrderStatus.APPROVED, new ArrayList<>(0));
        when(orderRepository.findByTrackingId(any(TrackingId.class))).thenReturn(Optional.of(order));
        when(orderDataMapper.orderToTrackOrderResponse(any(Order.class))).thenReturn(response);

        TrackOrderResponse result = orderTrackCommandHandler.trackOrder(query);

        assertEquals(response, result);
    }

    @Test
    void trackOrderThrowsExceptionWhenOrderNotFound() {
        UUID orderTrackingId = UUID.randomUUID();
        TrackOrderQuery query = new TrackOrderQuery(orderTrackingId);
        when(orderRepository.findByTrackingId(any(TrackingId.class))).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderTrackCommandHandler.trackOrder(query));

        assertEquals("Order not found. Tracking id: "+orderTrackingId, exception.getMessage());
    }
}