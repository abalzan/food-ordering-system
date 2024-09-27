package com.andrei.food.ordering.system.service.domain.outbox.scheduler.payment;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class PaymentOutboxCleanerSchedulerTest {

    @Mock
    private PaymentOutboxHelper paymentOutboxHelper;

    @InjectMocks
    private PaymentOutboxCleanerScheduler paymentOutboxCleanerScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processOutboxMessageDeletesCompletedMessages() {
        OrderPaymentOutboxMessage message = OrderPaymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .build();
        List<OrderPaymentOutboxMessage> messages = List.of(message);

        when(paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED, SagaStatus.COMPENSATED, SagaStatus.SUCCEEDED, SagaStatus.FAILED))
                .thenReturn(Optional.of(messages));

        paymentOutboxCleanerScheduler.processOutboxMessage();

        verify(paymentOutboxHelper, times(1)).deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED, SagaStatus.COMPENSATED, SagaStatus.SUCCEEDED, SagaStatus.FAILED);
    }

    @Test
    void processOutboxMessageHandlesEmptyMessages() {
        when(paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED, SagaStatus.COMPENSATED, SagaStatus.SUCCEEDED, SagaStatus.FAILED))
                .thenReturn(Optional.empty());

        paymentOutboxCleanerScheduler.processOutboxMessage();

        verify(paymentOutboxHelper, never()).deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
                any(OutboxStatus.class), any(SagaStatus[].class));
    }

    @Test
    void processOutboxMessageHandlesNullMessages() {
        when(paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED, SagaStatus.COMPENSATED, SagaStatus.SUCCEEDED, SagaStatus.FAILED))
                .thenReturn(Optional.empty());

        paymentOutboxCleanerScheduler.processOutboxMessage();

        verify(paymentOutboxHelper, never()).deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
                any(OutboxStatus.class), any(SagaStatus[].class));
    }
}