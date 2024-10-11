package com.andrei.food.ordering.system.restaurant.service.domain.outbox.scheduler;

import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class OrderOutboxSchedulerTest {

    @Mock
    private OrderOutboxHelper orderOutboxHelper;

    @Mock
    private RestaurantApprovalResponseMessagePublisher responseMessagePublisher;

    @InjectMocks
    private OrderOutboxScheduler orderOutboxScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Processes outbox messages successfully")
    void processesOutboxMessagesSuccessfully() {
        OrderOutboxMessage message = OrderOutboxMessage.builder().id(UUID.randomUUID()).build();
        List<OrderOutboxMessage> messages = Collections.singletonList(message);
        when(orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED)).thenReturn(Optional.of(messages));

        orderOutboxScheduler.processOutboxMessage();

        verify(responseMessagePublisher).publish(eq(message), any());
    }

    @Test
    @DisplayName("Handles no outbox messages to process")
    void handlesNoOutboxMessagesToProcess() {
        when(orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED)).thenReturn(Optional.empty());

        orderOutboxScheduler.processOutboxMessage();

        verify(responseMessagePublisher, never()).publish(any(), any());
        verify(orderOutboxHelper, never()).updateOutboxStatus(any(), any());
    }

    @Test
    @DisplayName("Handles exception during message processing")
    void handlesExceptionDuringMessageProcessing() {
        OrderOutboxMessage message = OrderOutboxMessage.builder().id(UUID.randomUUID()).build();
        List<OrderOutboxMessage> messages = Collections.singletonList(message);
        when(orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED)).thenReturn(Optional.of(messages));
        doThrow(new RuntimeException("Message bus error")).when(responseMessagePublisher).publish(eq(message), any());

        Assertions.assertThrows(RuntimeException.class, () -> orderOutboxScheduler.processOutboxMessage());

        verify(responseMessagePublisher).publish(eq(message), any());
        verify(orderOutboxHelper, never()).updateOutboxStatus(message, OutboxStatus.COMPLETED);
    }
}