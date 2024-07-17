package com.andrei.food.ordering.system.service.domain.ports.input.message.listener.payment;

import com.andrei.food.ordering.system.service.domain.dto.message.PaymentResponse;

public interface PaymentResponseMessageListener {

    void paymentCompleted(PaymentResponse paymentResponse);

    void paymentCancelled(PaymentResponse paymentResponse);
}
