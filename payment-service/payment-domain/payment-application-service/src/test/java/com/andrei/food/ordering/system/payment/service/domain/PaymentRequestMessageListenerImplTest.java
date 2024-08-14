package com.andrei.food.ordering.system.payment.service.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.domain.event.PaymentEvent;
import com.andrei.food.ordering.system.domain.valueobject.PaymentId;
import com.andrei.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

class PaymentRequestMessageListenerImplTest {

    @Mock
    private PaymentRequestHelper paymentRequestHelper;

    @InjectMocks
    private PaymentRequestMessageListenerImpl paymentRequestMessageListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void completePaymentSuccessfully() {
        PaymentRequest paymentRequest = PaymentRequest.builder().build();
        PaymentEvent paymentEvent = mock(PaymentEvent.class);
        Payment payment = mock(Payment.class);
        PaymentId paymentId = mock(PaymentId.class);
        OrderId orderId = mock(OrderId.class);
        UUID paymentUUID = UUID.randomUUID();
        UUID orderUUID = UUID.randomUUID();

        when(paymentId.getValue()).thenReturn(paymentUUID);
        when(orderId.getValue()).thenReturn(orderUUID);
        when(payment.getId()).thenReturn(paymentId);
        when(payment.getOrderId()).thenReturn(orderId);
        when(paymentEvent.getPayment()).thenReturn(payment);
        when(paymentRequestHelper.persistPayment(paymentRequest)).thenReturn(paymentEvent);

        paymentRequestMessageListener.completePayment(paymentRequest);

        verify(paymentRequestHelper).persistPayment(paymentRequest);
        verify(paymentEvent).fire();
    }

    @Test
    void completePaymentThrowsException() {
        PaymentRequest paymentRequest = PaymentRequest.builder().build();

        when(paymentRequestHelper.persistPayment(paymentRequest)).thenThrow(new RuntimeException("Error"));

        assertThrows(RuntimeException.class, () -> {
            paymentRequestMessageListener.completePayment(paymentRequest);
        });

        verify(paymentRequestHelper).persistPayment(paymentRequest);
    }

    @Test
    void cancelPaymentSuccessfully() {
        PaymentRequest paymentRequest = PaymentRequest.builder().build();
        PaymentEvent paymentEvent = mock(PaymentEvent.class);
        Payment payment = mock(Payment.class);
        PaymentId paymentId = mock(PaymentId.class);
        OrderId orderId = mock(OrderId.class);
        UUID paymentUUID = UUID.randomUUID();
        UUID orderUUID = UUID.randomUUID();

        when(paymentId.getValue()).thenReturn(paymentUUID);
        when(orderId.getValue()).thenReturn(orderUUID);
        when(payment.getId()).thenReturn(paymentId);
        when(payment.getOrderId()).thenReturn(orderId);
        when(paymentEvent.getPayment()).thenReturn(payment);
        when(paymentRequestHelper.persistCancelPayment(paymentRequest)).thenReturn(paymentEvent);

        paymentRequestMessageListener.cancelPayment(paymentRequest);

        verify(paymentRequestHelper).persistCancelPayment(paymentRequest);
        verify(paymentEvent).fire();
    }

    @Test
    void cancelPaymentThrowsException() {
        PaymentRequest paymentRequest = PaymentRequest.builder().build();

        when(paymentRequestHelper.persistCancelPayment(paymentRequest)).thenThrow(new RuntimeException("Error"));

        assertThrows(RuntimeException.class, () -> {
            paymentRequestMessageListener.cancelPayment(paymentRequest);
        });

        verify(paymentRequestHelper).persistCancelPayment(paymentRequest);
    }
}