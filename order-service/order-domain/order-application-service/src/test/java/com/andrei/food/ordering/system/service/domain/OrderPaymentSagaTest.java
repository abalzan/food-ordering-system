package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.OrderDomainService;
import com.andrei.food.ordering.system.service.domain.dto.message.PaymentResponse;
import com.andrei.food.ordering.system.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.andrei.food.ordering.system.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.andrei.food.ordering.system.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.andrei.food.ordering.system.service.domain.ports.output.repository.OrderRepository;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.event.OrderPaidEvent;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.service.valueobject.OrderStatus;
import com.andrei.food.ordering.system.service.valueobject.PaymentStatus;
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

class OrderPaymentSagaTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDomainService orderDomainService;

    @Mock
    private OrderSagaHelper orderSagaHelper;

    @Mock
    private PaymentOutboxHelper paymentOutboxHelper;

    @Mock
    private ApprovalOutboxHelper approvalOutboxHelper;

    @Mock
    private OrderDataMapper orderDataMapper;

    @InjectMocks
    private OrderPaymentSaga orderPaymentSaga;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processCompletesPaymentSuccessfully() {
        Order order = Order.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PENDING)
                .build();
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(order.getId().toString())
                .build();

        OrderPaidEvent orderPaidEvent = new OrderPaidEvent(order, ZonedDateTime.now());
        OrderPaymentOutboxMessage orderPaymentOutboxMessage = OrderPaymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .sagaStatus(SagaStatus.STARTED)
                .outboxStatus(OutboxStatus.STARTED)
                .version(0)
                .build();

        when(paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(any(UUID.class), eq(SagaStatus.STARTED)))
                .thenReturn(Optional.of(orderPaymentOutboxMessage));
        when(orderDataMapper.orderPaidEventToOrderApprovalEventPayload(any(OrderPaidEvent.class)))
                .thenReturn(OrderApprovalEventPayload.builder().build());
        when(orderSagaHelper.findOrder(any(String.class))).thenReturn(order);
        when(orderSagaHelper.orderStatusToSagaStatus(any(OrderStatus.class))).thenReturn(SagaStatus.PROCESSING);
        when(orderDomainService.payOrder(any(Order.class))).thenReturn(orderPaidEvent);

        orderPaymentSaga.process(paymentResponse);

        verify(orderDomainService, times(1)).payOrder(order);
        verify(paymentOutboxHelper, times(1)).save(any(OrderPaymentOutboxMessage.class));
        verify(approvalOutboxHelper, times(1)).saveApprovalOutboxMessage(any(OrderApprovalEventPayload.class), eq(OrderStatus.PENDING), eq(SagaStatus.PROCESSING), eq(OutboxStatus.STARTED), any(UUID.class));
    }

    @Test
    void processHandlesAlreadyProcessedOutboxMessage() {
        PaymentResponse paymentResponse = mock(PaymentResponse.class);

        when(paymentResponse.getSagaId()).thenReturn(UUID.randomUUID().toString());
        when(paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(any(UUID.class), eq(SagaStatus.STARTED)))
                .thenReturn(Optional.empty());

        orderPaymentSaga.process(paymentResponse);

        verify(orderDomainService, never()).payOrder(any(Order.class));
        verify(orderSagaHelper, never()).saveOrder(any(Order.class));
        verify(paymentOutboxHelper, never()).save(any(OrderPaymentOutboxMessage.class));
        verify(approvalOutboxHelper, never()).saveApprovalOutboxMessage(any(), any(OrderStatus.class), any(SagaStatus.class), any(OutboxStatus.class), any(UUID.class));
    }

    @Test
    void processHandlesNullResponseGracefully() {
        assertThrows(NullPointerException.class, () -> orderPaymentSaga.process(null));
    }

    @Test
    void rollbackCancelsPaymentSuccessfully() {
        Order order = Order.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PAID)
                .build();

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(order.getId().toString())
                .paymentStatus(PaymentStatus.CANCELLED)
                .build();

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = OrderApprovalOutboxMessage.builder()
                .orderStatus(OrderStatus.APPROVED)
                .sagaStatus(SagaStatus.PROCESSING)
                .build();
        OrderPaymentOutboxMessage orderPaymentOutboxMessage = mock(OrderPaymentOutboxMessage.class);

        when(paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(any(UUID.class), any(SagaStatus[].class)))
                .thenReturn(Optional.of(orderPaymentOutboxMessage));

        when(orderSagaHelper.findOrder(anyString())).thenReturn(order);
        when(orderSagaHelper.orderStatusToSagaStatus(any(OrderStatus.class))).thenReturn(SagaStatus.PROCESSING);
        when(approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(any(UUID.class), any(SagaStatus.class)))
                .thenReturn(Optional.of(orderApprovalOutboxMessage));

        orderPaymentSaga.rollback(paymentResponse);

        verify(orderDomainService, times(1)).cancelOrder(order, paymentResponse.getFailureMessages());
        verify(orderSagaHelper, times(1)).saveOrder(order);
        verify(paymentOutboxHelper, times(1)).save(any(OrderPaymentOutboxMessage.class));
        verify(approvalOutboxHelper, times(1)).save(any(OrderApprovalOutboxMessage.class));
    }

    @Test
    void rollbackHandlesAlreadyProcessedOutboxMessage() {
        Order order = Order.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PAID)
                .build();

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(order.getId().toString())
                .paymentStatus(PaymentStatus.CANCELLED)
                .build();

        when(paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(any(UUID.class), any(SagaStatus[].class)))
                .thenReturn(Optional.empty());

        orderPaymentSaga.rollback(paymentResponse);

        verify(orderDomainService, never()).cancelOrder(any(Order.class), anyList());
        verify(orderSagaHelper, never()).saveOrder(any(Order.class));
        verify(paymentOutboxHelper, never()).save(any(OrderPaymentOutboxMessage.class));
        verify(approvalOutboxHelper, never()).save(any(OrderApprovalOutboxMessage.class));
    }
}