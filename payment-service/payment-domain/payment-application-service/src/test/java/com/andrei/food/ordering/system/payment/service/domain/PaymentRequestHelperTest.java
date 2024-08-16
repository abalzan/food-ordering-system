package com.andrei.food.ordering.system.payment.service.domain;

import com.andrei.food.ordering.system.domain.PaymentDomainService;
import com.andrei.food.ordering.system.domain.entity.CreditEntry;
import com.andrei.food.ordering.system.domain.entity.CreditHistory;
import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.domain.event.PaymentEvent;
import com.andrei.food.ordering.system.domain.valueobject.CreditHistoryId;
import com.andrei.food.ordering.system.domain.valueobject.TransactionType;
import com.andrei.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.andrei.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.andrei.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.service.valueobject.Money;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentRequestHelperTest {
    @Mock
    private PaymentDomainService paymentDomainService;
    @Mock
    private PaymentDataMapper paymentDataMapper;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CreditEntryRepository creditEntryRepository;
    @Mock
    private CreditHistoryRepository creditHistoryRepository;
    @Mock
    private PaymentCompletedMessagePublisher paymentCompletedEventDomainEventPublisher;
    @Mock
    private PaymentCancelledMessagePublisher paymentCancelledEventDomainEventPublisher;
    @Mock
    private PaymentFailedMessagePublisher paymentFailedEventDomainEventPublisher;

    @InjectMocks
    private PaymentRequestHelper paymentRequestHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void persistPaymentSuccessfully() {
        UUID customerUUID = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .customerId(customerUUID.toString())
                .orderId(orderId.toString())
                .build();
        Payment payment = getPaymentBuild(customerUUID, orderId);
        CustomerId customerId = new CustomerId(UUID.fromString(paymentRequest.getCustomerId()));
        CreditEntry creditEntry = CreditEntry.builder().build();
        List<CreditHistory> creditHistories =  Collections.singletonList(getCreditHistories(customerId));

        when(paymentDataMapper.paymentRequestModelToPayment(paymentRequest)).thenReturn(payment);
        when(creditEntryRepository.findByCustomerId(customerId)).thenReturn(Optional.of(creditEntry));
        when(creditHistoryRepository.findByCustomerId(customerId)).thenReturn(Optional.of(creditHistories));
        when(paymentDomainService.validateAndInitiatePayment(any(), any(), any(), any(), any(), any())).thenReturn(mock(PaymentEvent.class));

        PaymentEvent result = paymentRequestHelper.persistPayment(paymentRequest);

        verify(paymentRepository).save(payment);
        verify(creditEntryRepository).save(creditEntry);
        verify(creditHistoryRepository).save(any(CreditHistory.class));
        assertNotNull(result);
    }

    @Test
    void persistPaymentThrowsExceptionWhenCreditEntryNotFound() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .customerId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();
        Payment payment = getPaymentBuild(UUID.randomUUID(), UUID.randomUUID());
        CustomerId customerId = new CustomerId(UUID.fromString(paymentRequest.getCustomerId()));

        when(paymentDataMapper.paymentRequestModelToPayment(paymentRequest)).thenReturn(payment);
        when(creditEntryRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThrows(PaymentApplicationServiceException.class, () -> {
            paymentRequestHelper.persistPayment(paymentRequest);
        });

        verify(paymentRepository, never()).save(any());
        verify(creditEntryRepository, never()).save(any());
        verify(creditHistoryRepository, never()).save(any());
    }

    @Test
    void persistPaymentThrowsExceptionWhenCreditHistoryNotFound() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .customerId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();
        Payment payment = getPaymentBuild(UUID.randomUUID(), UUID.randomUUID());
        CustomerId customerId = new CustomerId(UUID.fromString(paymentRequest.getCustomerId()));
        CreditEntry creditEntry = CreditEntry.builder().build();

        when(paymentDataMapper.paymentRequestModelToPayment(paymentRequest)).thenReturn(payment);
        when(creditEntryRepository.findByCustomerId(customerId)).thenReturn(Optional.of(creditEntry));
        when(creditHistoryRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThrows(PaymentApplicationServiceException.class, () -> {
            paymentRequestHelper.persistPayment(paymentRequest);
        });

        verify(paymentRepository, never()).save(any());
        verify(creditEntryRepository, never()).save(any());
        verify(creditHistoryRepository, never()).save(any());
    }

    @Test
    void persistCancelPaymentSuccessfully() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(UUID.randomUUID().toString())
                .customerId(customerId.getValue().toString())
                .build();
        Payment payment = getPaymentBuild(UUID.randomUUID(), UUID.randomUUID());

        CreditEntry creditEntry = CreditEntry.builder().build();
        List<CreditHistory> creditHistories = Collections.singletonList(getCreditHistories(customerId));

        when(paymentRepository.findByOrderId(any())).thenReturn(Optional.of(payment));
        when(creditEntryRepository.findByCustomerId(any())).thenReturn(Optional.of(creditEntry));
        when(creditHistoryRepository.findByCustomerId(any())).thenReturn(Optional.of(creditHistories));
        when(paymentDomainService.validateAndCancelPayment(any(), any(), any(), any(), any(), any())).thenReturn(mock(PaymentEvent.class));

        PaymentEvent result = paymentRequestHelper.persistCancelPayment(paymentRequest);

        verify(paymentRepository).save(payment);
        verify(creditEntryRepository).save(creditEntry);
        verify(creditHistoryRepository).save(any(CreditHistory.class));
        assertNotNull(result);
    }

    @Test
    void persistCancelPaymentThrowsExceptionWhenPaymentNotFound() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(UUID.randomUUID().toString())
                .build();

        when(paymentRepository.findByOrderId(any())).thenReturn(Optional.empty());

        assertThrows(PaymentApplicationServiceException.class, () -> {
            paymentRequestHelper.persistCancelPayment(paymentRequest);
        });

        verify(paymentRepository, never()).save(any());
        verify(creditEntryRepository, never()).save(any());
        verify(creditHistoryRepository, never()).save(any());
    }

    private static Payment getPaymentBuild(UUID customerUUID, UUID orderId) {
        return Payment.builder()
                .customerId(new CustomerId(customerUUID))
                .orderId(new OrderId(orderId))
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