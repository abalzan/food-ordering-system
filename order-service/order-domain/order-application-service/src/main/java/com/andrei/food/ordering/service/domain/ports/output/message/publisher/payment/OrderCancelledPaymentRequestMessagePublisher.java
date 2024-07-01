package com.andrei.food.ordering.service.domain.ports.output.message.publisher.payment;

import com.andrei.food.ordering.system.domain.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.domain.events.publisher.DomainEventPublisher;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
