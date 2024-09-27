package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.OrderDomainService;
import com.andrei.food.ordering.system.service.domain.dto.message.RestaurantApprovalResponse;
import com.andrei.food.ordering.system.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.andrei.food.ordering.system.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.andrei.food.ordering.system.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.service.valueobject.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderApprovalSagaTest {


    @Mock
    private OrderDomainService orderDomainService;

    @Mock
    private OrderSagaHelper orderSagaHelper;

    @Mock
    private ApprovalOutboxHelper approvalOutboxHelper;

    @Mock
    private PaymentOutboxHelper paymentOutboxHelper;

    @Mock
    private OrderDataMapper orderDataMapper;

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
        OrderApprovalOutboxMessage orderApprovalOutboxMessage = mock(OrderApprovalOutboxMessage.class);
        OrderPaymentOutboxMessage orderPaymentOutboxMessage = mock(OrderPaymentOutboxMessage.class);

        when(restaurantApprovalResponse.getSagaId()).thenReturn(UUID.randomUUID().toString());
        when(restaurantApprovalResponse.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(any(UUID.class), any(SagaStatus.class)))
                .thenReturn(Optional.of(orderApprovalOutboxMessage));
        when(orderSagaHelper.findOrder(anyString())).thenReturn(order);
        when(paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(any(UUID.class), any(SagaStatus.class)))
                .thenReturn(Optional.of(orderPaymentOutboxMessage));

        orderApprovalSaga.process(restaurantApprovalResponse);

        verify(orderDomainService, times(1)).ApproveOrder(order);
        verify(orderSagaHelper, times(1)).saveOrder(order);
        verify(approvalOutboxHelper, times(1)).save(any(OrderApprovalOutboxMessage.class));
        verify(paymentOutboxHelper, times(1)).save(any(OrderPaymentOutboxMessage.class));
    }

    @Test
    void processHandlesAlreadyProcessedOutboxMessage() {
        RestaurantApprovalResponse restaurantApprovalResponse = mock(RestaurantApprovalResponse.class);

        when(restaurantApprovalResponse.getSagaId()).thenReturn(UUID.randomUUID().toString());
        when(approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(any(UUID.class), any(SagaStatus.class)))
                .thenReturn(Optional.empty());

        orderApprovalSaga.process(restaurantApprovalResponse);

        verify(orderDomainService, never()).ApproveOrder(any(Order.class));
        verify(orderSagaHelper, never()).saveOrder(any(Order.class));
        verify(approvalOutboxHelper, never()).save(any(OrderApprovalOutboxMessage.class));
        verify(paymentOutboxHelper, never()).save(any(OrderPaymentOutboxMessage.class));
    }

    @Test
    void processHandlesNullResponseGracefully() {
        assertThrows(NullPointerException.class, () -> orderApprovalSaga.process(null));
    }


    @Test
    void rollbackCancelsApprovalSuccessfully() {
        UUID orderId = UUID.randomUUID();
        RestaurantApprovalResponse restaurantApprovalResponse = mock(RestaurantApprovalResponse.class);

        Order order = Order.builder()
                .orderId(new OrderId(orderId))
                .orderStatus(OrderStatus.CANCELLED)
                .build();

        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(order, ZonedDateTime.now());

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = mock(OrderApprovalOutboxMessage.class);

        when(restaurantApprovalResponse.getSagaId()).thenReturn(UUID.randomUUID().toString());
        when(restaurantApprovalResponse.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(any(UUID.class), eq(SagaStatus.PROCESSING)))
                .thenReturn(Optional.of(orderApprovalOutboxMessage));
        when(orderSagaHelper.findOrder(anyString())).thenReturn(order);
        when(orderSagaHelper.orderStatusToSagaStatus(any(OrderStatus.class))).thenReturn(SagaStatus.COMPENSATING);
        when(orderDomainService.cancelOrderPayment(order, restaurantApprovalResponse.getFailureMessages())).thenReturn(orderCancelledEvent);

        orderApprovalSaga.rollback(restaurantApprovalResponse);

        verify(orderDomainService, times(1)).cancelOrderPayment(order, restaurantApprovalResponse.getFailureMessages());
        verify(orderSagaHelper, times(1)).saveOrder(order);
        verify(approvalOutboxHelper, times(1)).save(any(OrderApprovalOutboxMessage.class));
        verify(paymentOutboxHelper, times(1)).savePaymentOutboxMessage(any(), eq(OrderStatus.CANCELLED), eq(SagaStatus.COMPENSATING), eq(OutboxStatus.STARTED), any(UUID.class));
    }

    @Test
    void rollbackHandlesAlreadyProcessedOutboxMessage() {
        RestaurantApprovalResponse restaurantApprovalResponse = mock(RestaurantApprovalResponse.class);

        when(restaurantApprovalResponse.getSagaId()).thenReturn(UUID.randomUUID().toString());
        when(approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(any(UUID.class), eq(SagaStatus.PROCESSING)))
                .thenReturn(Optional.empty());

        orderApprovalSaga.rollback(restaurantApprovalResponse);

        verify(orderDomainService, never()).cancelOrderPayment(any(Order.class), anyList());
        verify(orderSagaHelper, never()).saveOrder(any(Order.class));
        verify(approvalOutboxHelper, never()).save(any(OrderApprovalOutboxMessage.class));
        verify(paymentOutboxHelper, never()).savePaymentOutboxMessage(any(), any(OrderStatus.class), any(SagaStatus.class), any(OutboxStatus.class), any(UUID.class));
    }

    @Test
    void rollbackHandlesNullResponseGracefully() {
        assertThrows(NullPointerException.class, () -> orderApprovalSaga.rollback(null));
    }
}