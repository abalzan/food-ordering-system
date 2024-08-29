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
    private OrderRepository orderRepository;

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
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));
        when(orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher)).thenReturn(orderPaidEvent);

        OrderPaidEvent result = orderPaymentSaga.process(paymentResponse);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(order);
        verify(orderDomainService, times(1)).payOrder(order, orderPaidRestaurantRequestMessagePublisher);
    }

    @Test
    void processThrowsOrderNotFoundException() {
        PaymentResponse paymentResponse = mock(PaymentResponse.class);

        when(paymentResponse.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderPaymentSaga.process(paymentResponse));

        assertEquals("Order with id " + paymentResponse.getOrderId() + " not found", exception.getMessage());
    }

    @Test
    void rollbackCancelsPaymentSuccessfully() {
        PaymentResponse paymentResponse = mock(PaymentResponse.class);
        Order order = Order.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .build();

        when(paymentResponse.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));

        EmptyEvent result = orderPaymentSaga.rollback(paymentResponse);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(order);
        verify(orderDomainService, times(1)).cancelOrder(order, paymentResponse.getFailureMessages());
    }

    @Test
    void rollbackThrowsOrderNotFoundException() {
        PaymentResponse paymentResponse = mock(PaymentResponse.class);

        when(paymentResponse.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderPaymentSaga.rollback(paymentResponse));

        assertEquals("Order with id " + paymentResponse.getOrderId() + " not found", exception.getMessage());
    }
}