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
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.service.valueobject.Money;
import com.andrei.food.ordering.system.service.valueobject.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @BeforeEach
    void setUp() {
        paymentDomainService = new PaymentDomainServiceImpl();
        payment = mock(Payment.class);
        creditEntry = mock(CreditEntry.class);
        creditHistories = new ArrayList<>();
        failureMessages = new ArrayList<>();
    }

    @Test
    @DisplayName("Initiates payment with exact credit amount")
    void initiatesPaymentWithExactCreditAmount() {
        payment = Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .price(new Money(new BigDecimal(200)))
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

        PaymentCompletedEvent event = (PaymentCompletedEvent) paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories, failureMessages);

        assertTrue(failureMessages.isEmpty());
        assertEquals(PaymentStatus.COMPLETED, payment.getPaymentStatus());
        assertNotNull(event);
    }

    @Test
    @DisplayName("Fails payment with insufficient credit history")
    void failsPaymentWithInsufficientCreditHistory() {
        payment = Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .price(new Money(new BigDecimal(300)))
                .build();
        creditEntry = CreditEntry.builder()
                .creditEntryId(new CreditEntryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .totalCreditAmount(new Money(new BigDecimal(300)))
                .build();
        creditHistories.add(CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .customerId(creditEntry.getCustomerId())
                .transactionType(TransactionType.CREDIT)
                .amount(new Money(new BigDecimal(200)))
                .build());

        PaymentFailedEvent event = (PaymentFailedEvent) paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories, failureMessages);

        assertFalse(failureMessages.isEmpty());
        assertEquals(PaymentStatus.FAILED, payment.getPaymentStatus());
        assertNotNull(event);
    }

    @Test
    @DisplayName("Cancels payment with sufficient credit history")
    void cancelsPaymentWithSufficientCreditHistory() {
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
                .transactionType(TransactionType.DEBIT)
                .amount(new Money(new BigDecimal(100)))
                .build());

        PaymentCancelledEvent event = (PaymentCancelledEvent) paymentDomainService.validateAndCancelPayment(payment, creditEntry, creditHistories, failureMessages);

        assertTrue(failureMessages.isEmpty());
        assertEquals(PaymentStatus.CANCELLED, payment.getPaymentStatus());
        assertNotNull(event);
    }

    @Test
    @DisplayName("Fails cancellation with mismatched credit history")
    void failsCancellationWithMismatchedCreditHistory() {
        payment = Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .price(new Money(new BigDecimal(0)))
                .build();
        creditEntry = CreditEntry.builder()
                .creditEntryId(new CreditEntryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .totalCreditAmount(new Money(new BigDecimal(200)))
                .build();
        creditHistories.add(CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .transactionType(TransactionType.DEBIT)
                .amount(new Money(new BigDecimal(50)))
                .build());

        PaymentFailedEvent event = (PaymentFailedEvent) paymentDomainService.validateAndCancelPayment(payment, creditEntry, creditHistories, failureMessages);

        assertFalse(failureMessages.isEmpty());
        assertEquals(PaymentStatus.FAILED, payment.getPaymentStatus());
        assertNotNull(event);
    }
}