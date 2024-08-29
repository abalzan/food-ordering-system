package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.service.OrderDomainService;
import com.andrei.food.ordering.system.service.domain.dto.message.PaymentResponse;
import com.andrei.food.ordering.system.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.andrei.food.ordering.system.service.domain.ports.output.repository.OrderRepository;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.event.OrderPaidEvent;
import com.andrei.food.ordering.system.service.events.EmptyEvent;
import com.andrei.food.ordering.system.service.exception.OrderNotFoundException;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderPaymentSagaTest {

    @Mock
    private OrderDomainService orderDomainService;

    @Mock
    private OrderSagaHelper orderSagaHelper;

    @Mock
    private OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

    @InjectMocks
    private OrderPaymentSaga orderPaymentSaga;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processCompletesPaymentSuccessfully() {
        PaymentResponse paymentResponse = mock(PaymentResponse.class);
        Order order = Order.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .build();
        OrderPaidEvent orderPaidEvent = mock(OrderPaidEvent.class);

        when(paymentResponse.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(orderSagaHelper.findOrder(any(String.class))).thenReturn(order);
        when(orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher)).thenReturn(orderPaidEvent);

        OrderPaidEvent result = orderPaymentSaga.process(paymentResponse);

        assertNotNull(result);
        verify(orderSagaHelper, times(1)).saveOrder(order);
        verify(orderDomainService, times(1)).payOrder(order, orderPaidRestaurantRequestMessagePublisher);
    }

    @Test
    void rollbackCancelsPaymentSuccessfully() {
        PaymentResponse paymentResponse = mock(PaymentResponse.class);
        Order order = Order.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .build();

        when(paymentResponse.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(orderSagaHelper.findOrder(any(String.class))).thenReturn(order);

        EmptyEvent result = orderPaymentSaga.rollback(paymentResponse);

        assertNotNull(result);
        verify(orderSagaHelper, times(1)).saveOrder(order);
        verify(orderDomainService, times(1)).cancelOrder(order, paymentResponse.getFailureMessages());
    }
}