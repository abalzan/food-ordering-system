package com.andrei.food.ordering.system.service.domain.outbox.scheduler.payment;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.andrei.food.ordering.system.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class PaymentOutboxSchedulerTest {

    @Mock
    private PaymentOutboxHelper paymentOutboxHelper;

    @Mock
    private PaymentRequestMessagePublisher paymentRequestMessagePublisher;

    @InjectMocks
    private PaymentOutboxScheduler paymentOutboxScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processOutboxMessageProcessesStartedMessages() {
        OrderPaymentOutboxMessage message = OrderPaymentOutboxMessage.builder().id(UUID.randomUUID()).build();
        List<OrderPaymentOutboxMessage> messages = List.of(message);

        when(paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATING))
                .thenReturn(Optional.of(messages));

        paymentOutboxScheduler.processOutboxMessage();

        verify(paymentRequestMessagePublisher, times(1)).publish(eq(message), any());
    }

    @Test
    void processOutboxMessageHandlesEmptyMessages() {
        when(paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATING))
                .thenReturn(Optional.empty());

        paymentOutboxScheduler.processOutboxMessage();

        verify(paymentRequestMessagePublisher, never()).publish(any(), any());
    }

    @Test
    void processOutboxMessageHandlesNullMessages() {
        when(paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATING))
                .thenReturn(Optional.empty());

        paymentOutboxScheduler.processOutboxMessage();

        verify(paymentRequestMessagePublisher, never()).publish(any(), any());
    }
}