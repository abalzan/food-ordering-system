package com.andrei.food.ordering.system.service.dataaccess.outbox.payment.adapter;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.dataaccess.outbox.payment.entity.PaymentOutboxEntity;
import com.andrei.food.ordering.system.service.dataaccess.outbox.payment.exception.PaymentOutboxNotFoundException;
import com.andrei.food.ordering.system.service.dataaccess.outbox.payment.mapper.PaymentOutboxDataAccessMapper;
import com.andrei.food.ordering.system.service.dataaccess.outbox.payment.repository.PaymentOutboxJpaRepository;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
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

class PaymentOutboxRepositoryImplTest {

    @Mock
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Mock
    private PaymentOutboxDataAccessMapper paymentOutboxDataAccessMapper;

    @InjectMocks
    private PaymentOutboxRepositoryImpl paymentOutboxRepositoryImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveReturnsSavedMessage() {
        OrderPaymentOutboxMessage message = OrderPaymentOutboxMessage.builder().id(UUID.randomUUID()).build();
        when(paymentOutboxDataAccessMapper.orderPaymentOutboxMessageToOutboxEntity(any(OrderPaymentOutboxMessage.class)))
                .thenReturn(new PaymentOutboxEntity());
        when(paymentOutboxJpaRepository.save(any(PaymentOutboxEntity.class)))
                .thenReturn(new PaymentOutboxEntity());
        when(paymentOutboxDataAccessMapper.paymentOutboxEntityToOrderPaymentOutboxMessage(any(PaymentOutboxEntity.class)))
                .thenReturn(message);

        OrderPaymentOutboxMessage result = paymentOutboxRepositoryImpl.save(message);

        assertEquals(message, result);
    }

    @Test
    void findByTypeAndOutboxStatusAndSagaStatusReturnsMessages() {
        List<PaymentOutboxEntity> entities = List.of(PaymentOutboxEntity.builder().id(UUID.randomUUID()).build());
        when(paymentOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(anyString(), any(OutboxStatus.class), anyList()))
                .thenReturn(Optional.of(entities));
        when(paymentOutboxDataAccessMapper.paymentOutboxEntityToOrderPaymentOutboxMessage(any(PaymentOutboxEntity.class)))
                .thenReturn(OrderPaymentOutboxMessage.builder().id(UUID.randomUUID()).build());

        Optional<List<OrderPaymentOutboxMessage>> result = paymentOutboxRepositoryImpl.findByTypeAndOutboxStatusAndSagaStatus("type", OutboxStatus.STARTED, SagaStatus.STARTED);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    @Test
    void findByTypeAndOutboxStatusAndSagaStatusThrowsExceptionWhenNotFound() {
        when(paymentOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(anyString(), any(OutboxStatus.class), anyList()))
                .thenReturn(Optional.empty());

        assertThrows(PaymentOutboxNotFoundException.class, () -> paymentOutboxRepositoryImpl.findByTypeAndOutboxStatusAndSagaStatus("type", OutboxStatus.STARTED, SagaStatus.STARTED));
    }

    @Test
    void findByTypeAndSagaIdAndSagaStatusReturnsMessage() {
        PaymentOutboxEntity entity = PaymentOutboxEntity.builder().id(UUID.randomUUID()).build();
        when(paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(anyString(), any(UUID.class), anyList()))
                .thenReturn(Optional.of(entity));
        when(paymentOutboxDataAccessMapper.paymentOutboxEntityToOrderPaymentOutboxMessage(any(PaymentOutboxEntity.class)))
                .thenReturn(OrderPaymentOutboxMessage.builder().id(UUID.randomUUID()).build());

        Optional<OrderPaymentOutboxMessage> result = paymentOutboxRepositoryImpl.findByTypeAndSagaIdAndSagaStatus("type", UUID.randomUUID(), SagaStatus.STARTED);

        assertTrue(result.isPresent());
    }

    @Test
    void findByTypeAndSagaIdAndSagaStatusReturnsEmptyWhenNotFound() {
        when(paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(anyString(), any(UUID.class), anyList()))
                .thenReturn(Optional.empty());

        Optional<OrderPaymentOutboxMessage> result = paymentOutboxRepositoryImpl.findByTypeAndSagaIdAndSagaStatus("type", UUID.randomUUID(), SagaStatus.STARTED);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteByTypeAndOutboxStatusAndSagaStatusDeletesMessages() {
        doNothing().when(paymentOutboxJpaRepository).deleteByTypeAndOutboxStatusAndSagaStatusIn(anyString(), any(OutboxStatus.class), anyList());

        paymentOutboxRepositoryImpl.deleteByTypeAndOutboxStatusAndSagaStatus("type", OutboxStatus.STARTED, SagaStatus.STARTED);

        verify(paymentOutboxJpaRepository, times(1)).deleteByTypeAndOutboxStatusAndSagaStatusIn(anyString(), any(OutboxStatus.class), anyList());
    }
}