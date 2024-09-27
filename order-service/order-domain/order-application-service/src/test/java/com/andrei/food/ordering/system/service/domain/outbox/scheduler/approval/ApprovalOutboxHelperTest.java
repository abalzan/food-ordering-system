package com.andrei.food.ordering.system.service.domain.outbox.scheduler.approval;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.andrei.food.ordering.system.service.domain.ports.output.repository.ApprovalOutboxRepository;
import com.andrei.food.ordering.system.service.exception.OrderDomainException;
import com.andrei.food.ordering.system.service.valueobject.OrderStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApprovalOutboxHelperTest {

    @Mock
    private ApprovalOutboxRepository approvalOutboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ApprovalOutboxHelper approvalOutboxHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getApprovalOutboxMessageByOutboxStatusAndSagaStatusReturnsMessages() {
        List<OrderApprovalOutboxMessage> messages = List.of(OrderApprovalOutboxMessage.builder().id(UUID.randomUUID()).build());
        when(approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(anyString(), any(OutboxStatus.class), any(SagaStatus[].class)))
                .thenReturn(Optional.of(messages));

        Optional<List<OrderApprovalOutboxMessage>> result = approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus.STARTED, SagaStatus.STARTED);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    @Test
    void getApprovalOutboxMessageByOutboxStatusAndSagaStatusReturnsEmpty() {
        when(approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(anyString(), any(OutboxStatus.class), any(SagaStatus[].class)))
                .thenReturn(Optional.empty());

        Optional<List<OrderApprovalOutboxMessage>> result = approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus.STARTED, SagaStatus.STARTED);

        assertFalse(result.isPresent());
    }

    @Test
    void getApprovalOutboxMessageBySagaIdAndSagaStatusReturnsMessage() {
        OrderApprovalOutboxMessage message = OrderApprovalOutboxMessage.builder().id(UUID.randomUUID()).build();
        when(approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(anyString(), any(UUID.class), any(SagaStatus[].class)))
                .thenReturn(Optional.of(message));

        Optional<OrderApprovalOutboxMessage> result = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID.randomUUID(), SagaStatus.STARTED);

        assertTrue(result.isPresent());
    }

    @Test
    void getApprovalOutboxMessageBySagaIdAndSagaStatusReturnsEmpty() {
        when(approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(anyString(), any(UUID.class), any(SagaStatus[].class)))
                .thenReturn(Optional.empty());

        Optional<OrderApprovalOutboxMessage> result = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID.randomUUID(), SagaStatus.STARTED);

        assertFalse(result.isPresent());
    }

    @Test
    void saveThrowsExceptionWhenSaveFails() {
        UUID id = UUID.randomUUID();
        OrderApprovalOutboxMessage message = OrderApprovalOutboxMessage.builder().id(id).build();
        when(approvalOutboxRepository.save(any(OrderApprovalOutboxMessage.class))).thenReturn(null);

        OrderDomainException exception = assertThrows(OrderDomainException.class, () -> approvalOutboxHelper.save(message));

        assertEquals("Failed to save OrderApprovalOutboxMessage with id "+id, exception.getMessage());
    }

    @Test
    void saveApprovalOutboxMessageSerializesPayload() throws JsonProcessingException {
        OrderApprovalEventPayload payload = OrderApprovalEventPayload.builder()
                .orderId(UUID.randomUUID().toString())
                .createdAt(ZonedDateTime.now())
                .build();
        when(objectMapper.writeValueAsString(any(OrderApprovalEventPayload.class))).thenReturn("payload");
        when(approvalOutboxRepository.save(any(OrderApprovalOutboxMessage.class))).thenReturn(OrderApprovalOutboxMessage.builder().id(UUID.randomUUID()).build());

        approvalOutboxHelper.saveApprovalOutboxMessage(payload, OrderStatus.PAID, SagaStatus.PROCESSING, OutboxStatus.STARTED, UUID.randomUUID());

        verify(approvalOutboxRepository, times(1)).save(any(OrderApprovalOutboxMessage.class));
    }

    @Test
    void saveApprovalOutboxMessageThrowsExceptionWhenSerializationFails() throws JsonProcessingException {
        OrderApprovalEventPayload payload = OrderApprovalEventPayload.builder()
                .orderId(UUID.randomUUID().toString())
                .createdAt(ZonedDateTime.now())
                .build();
        when(objectMapper.writeValueAsString(any(OrderApprovalEventPayload.class))).thenThrow(JsonProcessingException.class);

        OrderDomainException exception = assertThrows(OrderDomainException.class, () -> approvalOutboxHelper.saveApprovalOutboxMessage(payload, OrderStatus.PAID, SagaStatus.PROCESSING, OutboxStatus.STARTED, UUID.randomUUID()));

        assertTrue(exception.getMessage().contains("Failed to serialize OrderApprovalEventPayload with id"));
    }

    @Test
    void deleteApprovalOutboxMessageByOutboxStatusAndSagaStatusDeletesMessages() {
        approvalOutboxHelper.deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus.STARTED, SagaStatus.STARTED);

        verify(approvalOutboxRepository, times(1)).deleteByTypeAndOutboxStatusAndSagaStatus(anyString(), any(OutboxStatus.class), any(SagaStatus[].class));
    }
}