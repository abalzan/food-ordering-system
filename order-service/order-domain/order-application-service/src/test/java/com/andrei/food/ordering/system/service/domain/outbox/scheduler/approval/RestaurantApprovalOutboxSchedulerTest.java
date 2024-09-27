package com.andrei.food.ordering.system.service.domain.outbox.scheduler.approval;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.andrei.food.ordering.system.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RestaurantApprovalOutboxSchedulerTest {

    @Mock
    private ApprovalOutboxHelper approvalOutboxHelper;

    @Mock
    private RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher;

    @InjectMocks
    private RestaurantApprovalOutboxScheduler restaurantApprovalOutboxScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processOutboxMessageProcessesStartedMessages() {
        OrderApprovalOutboxMessage message = OrderApprovalOutboxMessage.builder()
                .id(UUID.randomUUID())
                .build();
        List<OrderApprovalOutboxMessage> messages = List.of(message);

        when(approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.STARTED, SagaStatus.PROCESSING))
                .thenReturn(Optional.of(messages));

        restaurantApprovalOutboxScheduler.processOutboxMessage();

        verify(restaurantApprovalRequestMessagePublisher, times(1)).publish(eq(message), any());
    }

    @Test
    void processOutboxMessageHandlesEmptyMessages() {
        when(approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.STARTED, SagaStatus.PROCESSING))
                .thenReturn(Optional.empty());

        restaurantApprovalOutboxScheduler.processOutboxMessage();

        verify(restaurantApprovalRequestMessagePublisher, never()).publish(any(), any());
    }

    @Test
    void processOutboxMessageHandlesNullMessages() {
        when(approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.STARTED, SagaStatus.PROCESSING))
                .thenReturn(Optional.empty());

        restaurantApprovalOutboxScheduler.processOutboxMessage();

        verify(restaurantApprovalRequestMessagePublisher, never()).publish(any(), any());
    }
}