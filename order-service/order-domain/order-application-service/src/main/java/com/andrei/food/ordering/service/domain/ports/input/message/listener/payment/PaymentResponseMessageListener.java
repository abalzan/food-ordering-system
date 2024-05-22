package com.andrei.food.ordering.service.domain.ports.input.message.listener.payment;

import com.andrei.food.ordering.service.domain.dto.message.PaymentResponse;

public interface PaymentResponseMessageListener {

    void paymentCompleted(PaymentResponse paymentResponse);

    void paymentCancelled(PaymentResponse paymentResponse);
}
