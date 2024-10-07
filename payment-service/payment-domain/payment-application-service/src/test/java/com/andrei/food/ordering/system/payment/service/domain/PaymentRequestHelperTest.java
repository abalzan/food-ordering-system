package com.andrei.food.ordering.system.payment.service.domain;

import com.andrei.food.ordering.system.domain.PaymentDomainService;
import com.andrei.food.ordering.system.domain.entity.CreditEntry;
import com.andrei.food.ordering.system.domain.entity.CreditHistory;
import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.domain.event.PaymentCancelledEvent;
import com.andrei.food.ordering.system.domain.event.PaymentCompletedEvent;
import com.andrei.food.ordering.system.domain.event.PaymentEvent;
import com.andrei.food.ordering.system.domain.valueobject.CreditHistoryId;
import com.andrei.food.ordering.system.domain.valueobject.TransactionType;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.andrei.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.andrei.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper;
import com.andrei.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.andrei.food.ordering.system.payment.service.domain.outbox.scheduler.OrderOutboxHelper;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.service.valueobject.Money;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.service.valueobject.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentRequestHelperTest {
    @Mock
    private OrderOutboxHelper orderOutboxHelper;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CreditEntryRepository creditEntryRepository;
    @Mock
    private CreditHistoryRepository creditHistoryRepository;
    @Mock
    private PaymentDataMapper paymentDataMapper;
    @Mock
    private PaymentDomainService paymentDomainService;
    @Mock
    private PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    @InjectMocks
    private PaymentRequestHelper paymentRequestHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Persists payment when outbox message not processed")
    void persistsPaymentWhenOutboxMessageNotProcessed() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();
        Payment payment = getPaymentBuild(UUID.randomUUID(), UUID.randomUUID(), PaymentStatus.COMPLETED);
        CreditEntry creditEntry = CreditEntry.builder().build();
        List<CreditHistory> creditHistories = List.of(CreditHistory.builder().build());

        PaymentEvent paymentEvent = new PaymentCompletedEvent(payment, ZonedDateTime.now(ZoneId.of("UTC")));

        when(orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(any(), eq(PaymentStatus.COMPLETED)))
                .thenReturn(Optional.empty());
        when(paymentDataMapper.paymentRequestModelToPayment(any())).thenReturn(payment);
        when(creditEntryRepository.findByCustomerId(any())).thenReturn(Optional.of(creditEntry));
        when(creditHistoryRepository.findByCustomerId(any())).thenReturn(Optional.of(creditHistories));
        when(paymentDomainService.validateAndInitiatePayment(any(), any(), any(), any())).thenReturn(paymentEvent);

        paymentRequestHelper.persistPayment(paymentRequest);

        verify(paymentRepository).save(payment);
        verify(creditEntryRepository).save(creditEntry);
        verify(creditHistoryRepository).save(creditHistories.get(0));
        verify(orderOutboxHelper).saveOrderOutboxMessage(any(), eq(PaymentStatus.COMPLETED), eq(OutboxStatus.STARTED), any());
    }

    @Test
    @DisplayName("Does not persist payment when outbox message already processed")
    void doesNotPersistPaymentWhenOutboxMessageAlreadyProcessed() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();

        when(orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(any(), eq(PaymentStatus.COMPLETED)))
                .thenReturn(Optional.of(mock(OrderOutboxMessage.class)));

        paymentRequestHelper.persistPayment(paymentRequest);

        verify(paymentRepository, never()).save(any());
        verify(creditEntryRepository, never()).save(any());
        verify(creditHistoryRepository, never()).save(any());
        verify(orderOutboxHelper, never()).saveOrderOutboxMessage(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Throws exception when credit entry not found during persist payment")
    void throwsExceptionWhenCreditEntryNotFoundDuringPersistPayment() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();
        Payment payment = getPaymentBuild(UUID.randomUUID(), UUID.randomUUID(), PaymentStatus.COMPLETED);

        when(orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(any(), eq(PaymentStatus.COMPLETED)))
                .thenReturn(Optional.empty());
        when(paymentDataMapper.paymentRequestModelToPayment(any())).thenReturn(payment);
        when(creditEntryRepository.findByCustomerId(any())).thenReturn(Optional.empty());

        assertThrows(PaymentApplicationServiceException.class, () -> {
            paymentRequestHelper.persistPayment(paymentRequest);
        });

        verify(paymentRepository, never()).save(any());
        verify(creditEntryRepository, never()).save(any());
        verify(creditHistoryRepository, never()).save(any());
        verify(orderOutboxHelper, never()).saveOrderOutboxMessage(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Throws exception when credit history not found during persist payment")
    void throwsExceptionWhenCreditHistoryNotFoundDuringPersistPayment() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();
        Payment payment = getPaymentBuild(UUID.randomUUID(), UUID.randomUUID(), PaymentStatus.COMPLETED);
        CreditEntry creditEntry = CreditEntry.builder().build();

        when(orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(any(), eq(PaymentStatus.COMPLETED)))
                .thenReturn(Optional.empty());
        when(paymentDataMapper.paymentRequestModelToPayment(any())).thenReturn(payment);
        when(creditEntryRepository.findByCustomerId(any())).thenReturn(Optional.of(creditEntry));
        when(creditHistoryRepository.findByCustomerId(any())).thenReturn(Optional.empty());

        assertThrows(PaymentApplicationServiceException.class, () -> {
            paymentRequestHelper.persistPayment(paymentRequest);
        });

        verify(paymentRepository, never()).save(any());
        verify(creditEntryRepository, never()).save(any());
        verify(creditHistoryRepository, never()).save(any());
        verify(orderOutboxHelper, never()).saveOrderOutboxMessage(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Persists cancel payment when outbox message not processed")
    void persistsCancelPaymentWhenOutboxMessageNotProcessed() {
        UUID sagaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .sagaId(sagaId.toString())
                .orderId(orderId.toString())
                .build();
        Payment payment = getPaymentBuild(UUID.randomUUID(), orderId, PaymentStatus.CANCELLED);
        CreditEntry creditEntry = CreditEntry.builder().build();
        List<CreditHistory> creditHistories = List.of(CreditHistory.builder().build());

        PaymentEvent paymentEvent = new PaymentCancelledEvent(payment, ZonedDateTime.now(ZoneId.of("UTC")));

        when(orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(any(), eq(PaymentStatus.CANCELLED)))
                .thenReturn(Optional.empty());
        when(paymentDataMapper.paymentRequestModelToPayment(any())).thenReturn(payment);
        when(creditEntryRepository.findByCustomerId(any())).thenReturn(Optional.of(creditEntry));
        when(creditHistoryRepository.findByCustomerId(any())).thenReturn(Optional.of(creditHistories));
        when(paymentDomainService.validateAndCancelPayment(any(), any(), any(), any())).thenReturn(paymentEvent);
        when(paymentRepository.findByOrderId(any())).thenReturn(Optional.of(payment));

        paymentRequestHelper.persistCancelPayment(paymentRequest);

        verify(paymentRepository).save(payment);
        verify(creditEntryRepository).save(creditEntry);
        verify(creditHistoryRepository).save(creditHistories.get(0));
        verify(orderOutboxHelper).saveOrderOutboxMessage(any(), eq(PaymentStatus.CANCELLED), eq(OutboxStatus.STARTED), any());
    }


    @Test
    @DisplayName("Persists cancel payment when outbox message already processed")
    void persistsCancelPaymentWhenOutboxMessageAlreadyProcessed() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();

        when(orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(any(), eq(PaymentStatus.CANCELLED)))
                .thenReturn(Optional.of(mock(OrderOutboxMessage.class)));

        paymentRequestHelper.persistCancelPayment(paymentRequest);

        verify(paymentRepository, never()).save(any());
        verify(creditEntryRepository, never()).save(any());
        verify(creditHistoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws exception when credit entry not found during cancel payment")
    void throwsExceptionWhenCreditEntryNotFoundDuringCancelPayment() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();
        Payment payment = getPaymentBuild(UUID.randomUUID(), UUID.randomUUID(), PaymentStatus.COMPLETED);

        when(paymentRepository.findByOrderId(any())).thenReturn(Optional.of(payment));
        when(creditEntryRepository.findByCustomerId(any())).thenReturn(Optional.empty());

        assertThrows(PaymentApplicationServiceException.class, () -> {
            paymentRequestHelper.persistCancelPayment(paymentRequest);
        });

        verify(paymentRepository, never()).save(any());
        verify(creditEntryRepository, never()).save(any());
        verify(creditHistoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws exception when credit history not found during cancel payment")
    void throwsExceptionWhenCreditHistoryNotFoundDuringCancelPayment() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();
        Payment payment = getPaymentBuild(UUID.randomUUID(), UUID.randomUUID(), PaymentStatus.COMPLETED);
        CreditEntry creditEntry = CreditEntry.builder().build();

        when(paymentRepository.findByOrderId(any())).thenReturn(Optional.of(payment));
        when(creditEntryRepository.findByCustomerId(any())).thenReturn(Optional.of(creditEntry));
        when(creditHistoryRepository.findByCustomerId(any())).thenReturn(Optional.empty());

        assertThrows(PaymentApplicationServiceException.class, () -> {
            paymentRequestHelper.persistCancelPayment(paymentRequest);
        });

        verify(paymentRepository, never()).save(any());
        verify(creditEntryRepository, never()).save(any());
        verify(creditHistoryRepository, never()).save(any());
    }

    private static Payment getPaymentBuild(UUID customerUUID, UUID orderId, PaymentStatus paymentStatus) {
        return Payment.builder()
                .customerId(new CustomerId(customerUUID))
                .orderId(new OrderId(orderId))
                .paymentStatus(paymentStatus)
                .build();
    }

    private static CreditHistory getCreditHistories(CustomerId customerId) {
        return CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .amount(new Money(new BigDecimal("100")))
                .customerId(customerId)
                .transactionType(TransactionType.CREDIT)
                .build();
    }
}