package com.andrei.food.ordering.system.payment.service.messaging.mapper;

import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.domain.event.PaymentCancelledEvent;
import com.andrei.food.ordering.system.domain.event.PaymentCompletedEvent;
import com.andrei.food.ordering.system.domain.event.PaymentFailedEvent;
import com.andrei.food.ordering.system.domain.valueobject.PaymentId;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.andrei.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.service.valueobject.Money;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.service.valueobject.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentMessagingDataMapperTest {

    @Mock
    private PaymentCompletedEvent paymentCompletedEvent;
    @Mock
    private PaymentCancelledEvent paymentCancelledEvent;
    @Mock
    private PaymentFailedEvent paymentFailedEvent;
    @Mock
    private PaymentResponseAvroModel paymentResponseAvroModel;

    @InjectMocks
    private PaymentMessagingDataMapper paymentMessagingDataMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Payment mockPayment(PaymentStatus paymentStatus) {
        return Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .orderId(new OrderId(UUID.randomUUID()))
                .price(new Money(new BigDecimal("100.0")))
                .createdAt(ZonedDateTime.now())
                .paymentStatus(paymentStatus)
                .build();
    }

    @Test
    void paymentCompletedEventToPaymentResponseAvroModel() {
        Payment payment = mockPayment(PaymentStatus.COMPLETED);

        when(paymentCompletedEvent.getPayment()).thenReturn(payment);
        when(paymentCompletedEvent.getCreatedAt()).thenReturn(ZonedDateTime.now());
        when(paymentCompletedEvent.getFailureMessages()).thenReturn(Collections.emptyList());

        paymentMessagingDataMapper.paymentCompletedEventToPaymentResponseAvroModel(paymentCompletedEvent);

        PaymentResponseAvroModel result = paymentMessagingDataMapper.paymentCompletedEventToPaymentResponseAvroModel(paymentCompletedEvent);

        assertNotNull(result);
        assertEquals(PaymentStatus.COMPLETED.name(), result.getPaymentStatus().name());
    }

    @Test
    void paymentCancelledEventToPaymentResponseAvroModel() {
        Payment payment = mockPayment(PaymentStatus.CANCELLED);

        when(paymentCancelledEvent.getPayment()).thenReturn(payment);
        when(paymentCancelledEvent.getCreatedAt()).thenReturn(ZonedDateTime.now());
        when(paymentCancelledEvent.getFailureMessages()).thenReturn(Collections.emptyList());

        paymentMessagingDataMapper.paymentCancelledEventToPaymentResponseAvroModel(paymentCancelledEvent);

        PaymentResponseAvroModel result = paymentMessagingDataMapper.paymentCancelledEventToPaymentResponseAvroModel(paymentCancelledEvent);

        assertNotNull(result);
        assertEquals(PaymentStatus.CANCELLED.name(), result.getPaymentStatus().name());
    }

    @Test
    void paymentFailedEventToPaymentResponseAvroModel() {
        Payment payment = mockPayment(PaymentStatus.FAILED);

        when(paymentFailedEvent.getPayment()).thenReturn(payment);
        when(paymentFailedEvent.getCreatedAt()).thenReturn(ZonedDateTime.now());
        when(paymentFailedEvent.getFailureMessages()).thenReturn(Collections.singletonList("Insufficient funds"));
        paymentMessagingDataMapper.paymentFailedEventToPaymentResponseAvroModel(paymentFailedEvent);


        PaymentResponseAvroModel result = paymentMessagingDataMapper.paymentFailedEventToPaymentResponseAvroModel(paymentFailedEvent);

        assertNotNull(result);
        assertEquals(PaymentStatus.FAILED.name(), result.getPaymentStatus().name());
        assertEquals("Insufficient funds", result.getFailureMessages().get(0));
    }
}