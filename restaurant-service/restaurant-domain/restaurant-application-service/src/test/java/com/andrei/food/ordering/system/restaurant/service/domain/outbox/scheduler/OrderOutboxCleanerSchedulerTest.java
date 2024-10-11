package com.andrei.food.ordering.system.restaurant.service.domain.outbox.scheduler;

import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

class OrderOutboxCleanerSchedulerTest {

    @Mock
    private OrderOutboxHelper orderOutboxHelper;

    @InjectMocks
    private OrderOutboxCleanerScheduler orderOutboxCleanerScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Processes outbox messages successfully")
    void processesOutboxMessagesSuccessfully() {
        when(orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED))
                .thenReturn(Optional.of(Collections.singletonList(OrderOutboxMessage.builder().id(UUID.randomUUID()).build())));

        orderOutboxCleanerScheduler.processOutboxMessage();

        verify(orderOutboxHelper).deleteOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);
    }

    @Test
    @DisplayName("Handles no outbox messages to process")
    void handlesNoOutboxMessagesToProcess() {
        when(orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED))
                .thenReturn(Optional.empty());

        orderOutboxCleanerScheduler.processOutboxMessage();

        verify(orderOutboxHelper, never()).deleteOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);
    }
}