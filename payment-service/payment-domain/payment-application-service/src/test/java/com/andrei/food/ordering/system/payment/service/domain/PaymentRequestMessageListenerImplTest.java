package com.andrei.food.ordering.system.payment.service.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    @DisplayName("Completes payment with valid request")
    void completesPaymentWithValidRequest() {
        PaymentRequest paymentRequest = PaymentRequest.builder().build();

        paymentRequestMessageListener.completePayment(paymentRequest);

        verify(paymentRequestHelper).persistPayment(paymentRequest);
    }

    @Test
    @DisplayName("Cancels payment with valid request")
    void cancelsPaymentWithValidRequest() {
        PaymentRequest paymentRequest = PaymentRequest.builder().build();

        paymentRequestMessageListener.cancelPayment(paymentRequest);

        verify(paymentRequestHelper).persistCancelPayment(paymentRequest);
    }

    @Test
    @DisplayName("Handles exception during complete payment")
    void handlesExceptionDuringCompletePayment() {
        PaymentRequest paymentRequest = PaymentRequest.builder().build();

        doThrow(new RuntimeException("Error")).when(paymentRequestHelper).persistPayment(paymentRequest);

        assertThrows(RuntimeException.class, () -> {
            paymentRequestMessageListener.completePayment(paymentRequest);
        });

        verify(paymentRequestHelper).persistPayment(paymentRequest);
    }

    @Test
    @DisplayName("Handles exception during cancel payment")
    void handlesExceptionDuringCancelPayment() {
        PaymentRequest paymentRequest = PaymentRequest.builder().build();

        doThrow(new RuntimeException("Error")).when(paymentRequestHelper).persistCancelPayment(paymentRequest);

        assertThrows(RuntimeException.class, () -> {
            paymentRequestMessageListener.cancelPayment(paymentRequest);
        });

        verify(paymentRequestHelper).persistCancelPayment(paymentRequest);
    }
}