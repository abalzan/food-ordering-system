package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.service.domain.dto.message.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentResponseMessageListenerImplTest {

    @Mock
    private OrderPaymentSaga orderPaymentSaga;

    @InjectMocks
    private PaymentResponseMessageListenerImpl paymentResponseMessageListenerImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void paymentCompletedPublishesOrderPaidEvent() {
        PaymentResponse paymentResponse = mock(PaymentResponse.class);

        paymentResponseMessageListenerImpl.paymentCompleted(paymentResponse);

        verify(orderPaymentSaga, times(1)).process(paymentResponse);
    }

    @Test
    void paymentCompletedHandlesNullPaymentResponseGracefully() {
        assertThrows(NullPointerException.class, () -> paymentResponseMessageListenerImpl.paymentCompleted(null));
    }

    @Test
    void paymentCancelledRollsBackOrder() {
        PaymentResponse paymentResponse = mock(PaymentResponse.class);

        paymentResponseMessageListenerImpl.paymentCancelled(paymentResponse);

        verify(orderPaymentSaga, times(1)).rollback(paymentResponse);
    }

    @Test
    void paymentCancelledHandlesNullPaymentResponseGracefully() {
        assertThrows(NullPointerException.class, () -> paymentResponseMessageListenerImpl.paymentCancelled(null));
    }
}