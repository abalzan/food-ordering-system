package com.andrei.food.ordering.system.payment.service.domain;

import com.andrei.food.ordering.system.domain.event.PaymentEvent;
import com.andrei.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.andrei.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private final PaymentRequestHelper paymentRequestHelper;

    @Override
    public void completePayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(paymentRequest);
        fireEvents(paymentEvent);
    }

    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(paymentRequest);
        fireEvents(paymentEvent);
    }

    private void fireEvents(PaymentEvent paymentEvent) {
        log.info("Publishing payment event with Payment id: {} and order id: {}",
                paymentEvent.getPayment().getId().getValue(), paymentEvent.getPayment().getOrderId().getValue());

        paymentEvent.fire();
    }
}
