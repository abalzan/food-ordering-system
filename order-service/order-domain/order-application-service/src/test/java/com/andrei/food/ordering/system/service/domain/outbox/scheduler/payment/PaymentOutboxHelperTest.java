package com.andrei.food.ordering.system.service.domain.outbox.scheduler.payment;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.andrei.food.ordering.system.service.domain.ports.output.repository.PaymentOutboxRepository;
import com.andrei.food.ordering.system.service.exception.OrderDomainException;
import com.andrei.food.ordering.system.service.valueobject.OrderStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentOutboxHelperTest {

    @Mock
    private PaymentOutboxRepository paymentOutboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentOutboxHelper paymentOutboxHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPaymentOutboxMessageByOutboxStatusAndSagaStatusReturnsMessages() {
        List<OrderPaymentOutboxMessage> messages = List.of(OrderPaymentOutboxMessage.builder().id(UUID.randomUUID()).build());
        when(paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(anyString(), any(OutboxStatus.class), any(SagaStatus[].class)))
                .thenReturn(Optional.of(messages));

        Optional<List<OrderPaymentOutboxMessage>> result = paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus.STARTED, SagaStatus.STARTED);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    @Test
    void getPaymentOutboxMessageByOutboxStatusAndSagaStatusReturnsEmpty() {
        when(paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(anyString(), any(OutboxStatus.class), any(SagaStatus[].class)))
                .thenReturn(Optional.empty());

        Optional<List<OrderPaymentOutboxMessage>> result = paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus.STARTED, SagaStatus.STARTED);

        assertFalse(result.isPresent());
    }

    @Test
    void getPaymentOutboxMessageBySagaIdAndSagaStatusReturnsMessage() {
        OrderPaymentOutboxMessage message = OrderPaymentOutboxMessage.builder().id(UUID.randomUUID()).build();
        when(paymentOutboxRepository.findByTypeAndSagaIdAndSagaStatus(anyString(), any(UUID.class), any(SagaStatus[].class)))
                .thenReturn(Optional.of(message));

        Optional<OrderPaymentOutboxMessage> result = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(UUID.randomUUID(), SagaStatus.STARTED);

        assertTrue(result.isPresent());
    }

    @Test
    void getPaymentOutboxMessageBySagaIdAndSagaStatusReturnsEmpty() {
        when(paymentOutboxRepository.findByTypeAndSagaIdAndSagaStatus(anyString(), any(UUID.class), any(SagaStatus[].class)))
                .thenReturn(Optional.empty());

        Optional<OrderPaymentOutboxMessage> result = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(UUID.randomUUID(), SagaStatus.STARTED);

        assertFalse(result.isPresent());
    }

    @Test
    void saveThrowsExceptionWhenSaveFails() {
        OrderPaymentOutboxMessage message = OrderPaymentOutboxMessage.builder().build();
        when(paymentOutboxRepository.save(any(OrderPaymentOutboxMessage.class))).thenReturn(null);

        OrderDomainException exception = assertThrows(OrderDomainException.class, () -> paymentOutboxHelper.save(message));

        assertEquals("Failed to save OrderPaymentOutboxMessage with id null", exception.getMessage());
    }

    @Test
    void savePaymentOutboxMessageSerializesPayload() throws JsonProcessingException {
        OrderPaymentEventPayload payload = OrderPaymentEventPayload.builder().orderId(UUID.randomUUID().toString()).build();
        when(objectMapper.writeValueAsString(any(OrderPaymentEventPayload.class))).thenReturn("payload");
        when(paymentOutboxRepository.save(any(OrderPaymentOutboxMessage.class))).thenReturn(OrderPaymentOutboxMessage.builder().build());

        paymentOutboxHelper.savePaymentOutboxMessage(payload, OrderStatus.PAID, SagaStatus.PROCESSING, OutboxStatus.STARTED, UUID.randomUUID());

        verify(paymentOutboxRepository, times(1)).save(any(OrderPaymentOutboxMessage.class));
    }

    @Test
    void savePaymentOutboxMessageThrowsOrderDomainException() throws JsonProcessingException {
        OrderPaymentEventPayload payload = OrderPaymentEventPayload.builder().orderId(UUID.randomUUID().toString()).build();
        when(objectMapper.writeValueAsString(any(OrderPaymentEventPayload.class))).thenReturn("payload");
        when(paymentOutboxRepository.save(any(OrderPaymentOutboxMessage.class))).thenReturn(null);

        assertThrows(OrderDomainException.class, () -> paymentOutboxHelper.savePaymentOutboxMessage(payload, OrderStatus.PAID, SagaStatus.PROCESSING, OutboxStatus.STARTED, UUID.randomUUID()));

        verify(paymentOutboxRepository, times(1)).save(any(OrderPaymentOutboxMessage.class));
    }


    @Test
    void savePaymentOutboxMessageThrowsExceptionWhenSerializationFails() throws JsonProcessingException {
        OrderPaymentEventPayload payload = OrderPaymentEventPayload.builder().orderId(UUID.randomUUID().toString()).build();
        when(objectMapper.writeValueAsString(any(OrderPaymentEventPayload.class))).thenThrow(JsonProcessingException.class);

        OrderDomainException exception = assertThrows(OrderDomainException.class, () -> paymentOutboxHelper.savePaymentOutboxMessage(payload, OrderStatus.PAID, SagaStatus.PROCESSING, OutboxStatus.STARTED, UUID.randomUUID()));

        assertTrue(exception.getMessage().contains("Failed to serialize OrderPaymentEventPayload for order id"));
    }

    @Test
    void deletePaymentOutboxMessageByOutboxStatusAndSagaStatusDeletesMessages() {
        paymentOutboxHelper.deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus.STARTED, SagaStatus.STARTED);

        verify(paymentOutboxRepository, times(1)).deleteByTypeAndOutboxStatusAndSagaStatus(anyString(), any(OutboxStatus.class), any(SagaStatus[].class));
    }
}