package com.andrei.food.ordering.system.domain.event;

import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCompletedEvent extends PaymentEvent {


    public PaymentCompletedEvent(Payment payment, ZonedDateTime createdAt) {
        super(payment, createdAt, Collections.emptyList());
    }
}
