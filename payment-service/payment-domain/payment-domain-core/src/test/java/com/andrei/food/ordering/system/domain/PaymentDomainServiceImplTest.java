package com.andrei.food.ordering.system.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.domain.entity.CreditEntry;
import com.andrei.food.ordering.system.domain.entity.CreditHistory;
import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.domain.event.PaymentCancelledEvent;
import com.andrei.food.ordering.system.domain.event.PaymentCompletedEvent;
import com.andrei.food.ordering.system.domain.event.PaymentFailedEvent;
import com.andrei.food.ordering.system.domain.valueobject.CreditEntryId;
import com.andrei.food.ordering.system.domain.valueobject.CreditHistoryId;
import com.andrei.food.ordering.system.domain.valueobject.PaymentId;
import com.andrei.food.ordering.system.domain.valueobject.TransactionType;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.service.valueobject.Money;
import com.andrei.food.ordering.system.service.valueobject.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class PaymentDomainServiceImplTest {

    private PaymentDomainServiceImpl paymentDomainService;
    private Payment payment;
    private CreditEntry creditEntry;
    private List<CreditHistory> creditHistories;
    private List<String> failureMessages;

    private DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher;
    private DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher;
    private DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher;

    @BeforeEach
    void setUp() {
        paymentDomainService = new PaymentDomainServiceImpl();
        payment = mock(Payment.class);
        creditEntry = mock(CreditEntry.class);
        creditHistories = new ArrayList<>();
        failureMessages = new ArrayList<>();
        paymentCompletedEventDomainEventPublisher = mock(DomainEventPublisher.class);
        paymentFailedEventDomainEventPublisher = mock(DomainEventPublisher.class);
        paymentCancelledEventDomainEventPublisher = mock(DomainEventPublisher.class);
    }

    @Test
    void validateAndInitiatePayment_SuccessfulPayment() {
        payment = Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .price(new Money(new BigDecimal(100)))
                .build();
        creditEntry = CreditEntry.builder()
                .creditEntryId(new CreditEntryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .totalCreditAmount(new Money(new BigDecimal(200)))
                .build();
        creditHistories.add(CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .customerId(creditEntry.getCustomerId())
                .transactionType(TransactionType.CREDIT)
                .amount(new Money(new BigDecimal(200)))
                .build());

        PaymentCompletedEvent event = (PaymentCompletedEvent) paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories, failureMessages, paymentCompletedEventDomainEventPublisher, paymentFailedEventDomainEventPublisher);

        assertTrue(failureMessages.isEmpty());
        assertEquals(PaymentStatus.COMPLETED, payment.getPaymentStatus());
        assertNotNull(event);
    }

    @Test
    void validateAndInitiatePayment_InsufficientCredit() {
        payment = Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .price(new Money(new BigDecimal(300)))
                .build();
        creditEntry = CreditEntry.builder()
                .creditEntryId(new CreditEntryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .totalCreditAmount(new Money(new BigDecimal(200)))
                .build();
        creditHistories.add(CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .customerId(creditEntry.getCustomerId())
                .transactionType(TransactionType.CREDIT)
                .amount(new Money(new BigDecimal(200)))
                .build());

        PaymentFailedEvent event = (PaymentFailedEvent) paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories, failureMessages, paymentCompletedEventDomainEventPublisher, paymentFailedEventDomainEventPublisher);

        assertFalse(failureMessages.isEmpty());
        assertEquals(PaymentStatus.FAILED, payment.getPaymentStatus());
        assertNotNull(event);
    }

    @Test
    void validateAndCancelPayment_SuccessfulCancellation() {
        payment = Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .price(new Money(new BigDecimal(100)))
                .build();

        PaymentCancelledEvent event = (PaymentCancelledEvent) paymentDomainService.validateAndCancelPayment(payment, creditEntry, creditHistories, failureMessages, paymentCancelledEventDomainEventPublisher, paymentFailedEventDomainEventPublisher);

        assertTrue(failureMessages.isEmpty());
        assertEquals(PaymentStatus.CANCELLED, payment.getPaymentStatus());
        assertNotNull(event);
    }

    @Test
    void validateAndCancelPayment_FailureDueToCreditHistoryMismatch() {
        payment = Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .price(new Money(new BigDecimal(0)))
                .build();
        creditEntry = CreditEntry.builder()
                .creditEntryId(new CreditEntryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .totalCreditAmount(new Money(new BigDecimal(100)))
                .build();

        creditHistories.add(CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .transactionType(TransactionType.DEBIT)
                .amount(new Money(new BigDecimal(50)))
                .build());

        PaymentFailedEvent event = (PaymentFailedEvent) paymentDomainService.validateAndCancelPayment(payment, creditEntry, creditHistories, failureMessages, paymentCancelledEventDomainEventPublisher, paymentFailedEventDomainEventPublisher);

        assertFalse(failureMessages.isEmpty());
        assertEquals(PaymentStatus.FAILED, payment.getPaymentStatus());
        assertNotNull(event);
    }
}