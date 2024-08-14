package com.andrei.food.ordering.system.payment.service.domain.ports.output.message.publisher;

import com.andrei.food.ordering.system.domain.event.PaymentCompletedEvent;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;

public interface PaymentCompletedMessagePublisher extends DomainEventPublisher<PaymentCompletedEvent> {

}
