package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.service.OrderDomainService;
import com.andrei.food.ordering.system.service.domain.dto.message.RestaurantApprovalResponse;
import com.andrei.food.ordering.system.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.service.events.EmptyEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderApprovalSagaTest {

    @Mock
    private OrderDomainService orderDomainService;

    @Mock
    private OrderSagaHelper orderSagaHelper;

    @Mock
    private OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;

    @InjectMocks
    private OrderApprovalSaga orderApprovalSaga;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processCompletesApprovalSuccessfully() {
        RestaurantApprovalResponse restaurantApprovalResponse = mock(RestaurantApprovalResponse.class);
        Order order = mock(Order.class);

        when(restaurantApprovalResponse.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(orderSagaHelper.findOrder(anyString())).thenReturn(order);

        EmptyEvent result = orderApprovalSaga.process(restaurantApprovalResponse);

        assertNotNull(result);
        verify(orderDomainService, times(1)).ApproveOrder(order);
        verify(orderSagaHelper, times(1)).saveOrder(order);
    }

    @Test
    void processHandlesNullResponseGracefully() {
        assertThrows(NullPointerException.class, () -> orderApprovalSaga.process(null));
    }

    @Test
    void rollbackCancelsApprovalSuccessfully() {
        RestaurantApprovalResponse restaurantApprovalResponse = mock(RestaurantApprovalResponse.class);
        Order order = mock(Order.class);
        OrderCancelledEvent orderCancelledEvent = mock(OrderCancelledEvent.class);

        when(restaurantApprovalResponse.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(orderSagaHelper.findOrder(anyString())).thenReturn(order);
        when(orderDomainService.cancelOrderPayment(order, restaurantApprovalResponse.getFailureMessages(), orderCancelledPaymentRequestMessagePublisher)).thenReturn(orderCancelledEvent);

        OrderCancelledEvent result = orderApprovalSaga.rollback(restaurantApprovalResponse);

        assertNotNull(result);
        verify(orderDomainService, times(1)).cancelOrderPayment(order, restaurantApprovalResponse.getFailureMessages(), orderCancelledPaymentRequestMessagePublisher);
        verify(orderSagaHelper, times(1)).saveOrder(order);
    }

    @Test
    void rollbackHandlesNullResponseGracefully() {
        assertThrows(NullPointerException.class, () -> orderApprovalSaga.rollback(null));
    }
}