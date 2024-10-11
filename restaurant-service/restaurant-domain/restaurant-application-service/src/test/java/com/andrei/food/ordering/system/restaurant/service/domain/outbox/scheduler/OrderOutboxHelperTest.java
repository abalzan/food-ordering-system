package com.andrei.food.ordering.system.restaurant.service.domain.outbox.scheduler;

import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.andrei.food.ordering.system.restaurant.service.domain.exception.RestaurantDomainException;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;
import com.andrei.food.ordering.system.service.DomainConstants;
import com.andrei.food.ordering.system.service.valueobject.OrderApprovalStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.andrei.food.ordering.system.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderOutboxHelperTest {

    @Mock
    private OrderOutboxRepository orderOutboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderOutboxHelper orderOutboxHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Gets order outbox message by outbox status successfully")
    void getsOrderOutboxMessageByOutboxStatusSuccessfully() {
        OrderOutboxMessage message = OrderOutboxMessage.builder().id(UUID.randomUUID()).build();
        when(orderOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, OutboxStatus.COMPLETED))
                .thenReturn(Optional.of(Collections.singletonList(message)));

        Optional<List<OrderOutboxMessage>> result = orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(message, result.get().get(0));
    }

    @Test
    @DisplayName("Handles no order outbox message by outbox status")
    void handlesNoOrderOutboxMessageByOutboxStatus() {
        when(orderOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, OutboxStatus.COMPLETED))
                .thenReturn(Optional.empty());

        Optional<List<OrderOutboxMessage>> result = orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Throws exception when creating payload fails")
    void throwsExceptionWhenCreatingPayloadFails() throws JsonProcessingException {
        OrderEventPayload payload = OrderEventPayload.builder().createdAt(ZonedDateTime.now()).build();
        when(objectMapper.writeValueAsString(payload)).thenThrow(JsonProcessingException.class);

        assertThrows(RestaurantDomainException.class, () -> orderOutboxHelper.saveOrderOutboxMessage(payload, OrderApprovalStatus.APPROVED, OutboxStatus.STARTED, UUID.randomUUID()));
    }

    @Test
    @DisplayName("Gets completed order outbox message by saga ID and status successfully")
    void getsCompletedOrderOutboxMessageBySagaIdAndStatusSuccessfully() {
        UUID sagaId = UUID.randomUUID();
        OrderOutboxMessage message = OrderOutboxMessage.builder().id(UUID.randomUUID()).build();
        when(orderOutboxRepository.findByTypeAndSagaIdAndOutboxStatus(ORDER_SAGA_NAME, sagaId, OutboxStatus.COMPLETED))
                .thenReturn(Optional.of(message));

        Optional<OrderOutboxMessage> result = orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(sagaId, OutboxStatus.COMPLETED);

        assertTrue(result.isPresent());
        assertEquals(message, result.get());
    }

    @Test
    @DisplayName("Handles no completed order outbox message by saga ID and status")
    void handlesNoCompletedOrderOutboxMessageBySagaIdAndStatus() {
        UUID sagaId = UUID.randomUUID();
        when(orderOutboxRepository.findByTypeAndSagaIdAndOutboxStatus(ORDER_SAGA_NAME, sagaId, OutboxStatus.COMPLETED))
                .thenReturn(Optional.empty());

        Optional<OrderOutboxMessage> result = orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(sagaId, OutboxStatus.COMPLETED);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deletes order outbox messages by status successfully")
    void deletesOrderOutboxMessagesByStatusSuccessfully() {
        doNothing().when(orderOutboxRepository).deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, OutboxStatus.COMPLETED);

        orderOutboxHelper.deleteOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);

        verify(orderOutboxRepository).deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, OutboxStatus.COMPLETED);
    }

    @Test
    @DisplayName("Handles exception when deleting order outbox messages by status fails")
    void handlesExceptionWhenDeletingOrderOutboxMessagesByStatusFails() {
        doThrow(new RuntimeException("Database error")).when(orderOutboxRepository).deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, OutboxStatus.COMPLETED);

        assertThrows(RuntimeException.class, () -> orderOutboxHelper.deleteOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED));
    }

    @Test
    @DisplayName("Saves order outbox message successfully")
    void savesOrderOutboxMessageSuccessfully() throws JsonProcessingException {
        OrderEventPayload payload = OrderEventPayload.builder().createdAt(ZonedDateTime.now()).build();
        OrderOutboxMessage message = OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .createdAt(payload.getCreatedAt())
                .processedAt(ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)))
                .type(ORDER_SAGA_NAME)
                .payload("payload")
                .payload("payload")
                .approvalStatus(OrderApprovalStatus.APPROVED)
                .outboxStatus(OutboxStatus.STARTED)
                .build();

        when(objectMapper.writeValueAsString(payload)).thenReturn("payload");
        when(orderOutboxRepository.save(any(OrderOutboxMessage.class))).thenReturn(message);

        orderOutboxHelper.saveOrderOutboxMessage(payload, OrderApprovalStatus.APPROVED, OutboxStatus.STARTED, UUID.randomUUID());

        verify(orderOutboxRepository).save(any(OrderOutboxMessage.class));
    }

    @Test
    @DisplayName("Handles exception when saving order outbox message with invalid payload")
    void handlesExceptionWhenSavingOrderOutboxMessageWithInvalidPayload() throws JsonProcessingException {
        OrderEventPayload payload = OrderEventPayload.builder().createdAt(ZonedDateTime.now()).build();
        when(objectMapper.writeValueAsString(payload)).thenThrow(JsonProcessingException.class);

        assertThrows(RestaurantDomainException.class, () -> orderOutboxHelper.saveOrderOutboxMessage(payload, OrderApprovalStatus.APPROVED, OutboxStatus.STARTED, UUID.randomUUID()));
    }

    @Test
    @DisplayName("Updates outbox status successfully")
    void updatesOutboxStatusSuccessfully() {
        OrderOutboxMessage message = OrderOutboxMessage.builder().id(UUID.randomUUID()).build();
        when(orderOutboxRepository.save(any(OrderOutboxMessage.class))).thenReturn(message);

        orderOutboxHelper.updateOutboxStatus(message, OutboxStatus.COMPLETED);

        assertEquals(OutboxStatus.COMPLETED, message.getOutboxStatus());
        verify(orderOutboxRepository).save(message);
    }

    @Test
    @DisplayName("Handles exception when updating outbox status fails")
    void handlesExceptionWhenUpdatingOutboxStatusFails() {
        OrderOutboxMessage message = OrderOutboxMessage.builder().id(UUID.randomUUID()).build();
        when(orderOutboxRepository.save(any(OrderOutboxMessage.class))).thenReturn(null);

        assertThrows(RestaurantDomainException.class, () -> orderOutboxHelper.updateOutboxStatus(message, OutboxStatus.COMPLETED));
    }
}