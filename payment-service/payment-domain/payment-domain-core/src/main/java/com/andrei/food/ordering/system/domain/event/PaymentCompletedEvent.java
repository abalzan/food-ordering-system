package com.andrei.food.ordering.system.domain.event;

import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCompletedEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher;

    public PaymentCompletedEvent(Payment payment, ZonedDateTime createdAt,
                                 DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher) {
        super(payment, createdAt, Collections.emptyList());
        this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
    }


    @Override
    public void fire() {
        paymentCompletedEventDomainEventPublisher.publish(this);
    }
}
