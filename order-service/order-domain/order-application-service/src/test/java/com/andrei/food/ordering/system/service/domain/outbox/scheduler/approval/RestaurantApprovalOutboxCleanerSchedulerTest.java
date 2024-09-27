package com.andrei.food.ordering.system.service.domain.outbox.scheduler.approval;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class RestaurantApprovalOutboxCleanerSchedulerTest {

    @Mock
    private ApprovalOutboxHelper approvalOutboxHelper;

    @InjectMocks
    private RestaurantApprovalOutboxCleanerScheduler restaurantApprovalOutboxCleanerScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processOutboxMessageDeletesCompletedMessages() {
        OrderApprovalOutboxMessage message = OrderApprovalOutboxMessage.builder()
            .id(UUID.randomUUID())
            .build();

        List<OrderApprovalOutboxMessage> messages = List.of(message);

        when(approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED, SagaStatus.COMPENSATED, SagaStatus.SUCCEEDED, SagaStatus.FAILED))
                .thenReturn(Optional.of(messages));

        restaurantApprovalOutboxCleanerScheduler.processOutboxMessage();

        verify(approvalOutboxHelper, times(1)).deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED, SagaStatus.COMPENSATED, SagaStatus.SUCCEEDED, SagaStatus.FAILED);
    }

    @Test
    void processOutboxMessageHandlesEmptyMessages() {
        when(approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED, SagaStatus.COMPENSATED, SagaStatus.SUCCEEDED, SagaStatus.FAILED))
                .thenReturn(Optional.empty());

        restaurantApprovalOutboxCleanerScheduler.processOutboxMessage();

        verify(approvalOutboxHelper, never()).deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                any(OutboxStatus.class), any(SagaStatus[].class));
    }

    @Test
    void processOutboxMessageHandlesNullMessages() {
        when(approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED, SagaStatus.COMPENSATED, SagaStatus.SUCCEEDED, SagaStatus.FAILED))
                .thenReturn(Optional.empty());

        restaurantApprovalOutboxCleanerScheduler.processOutboxMessage();

        verify(approvalOutboxHelper, never()).deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                any(OutboxStatus.class), any(SagaStatus[].class));
    }
}