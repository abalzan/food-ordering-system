package com.andrei.food.ordering.system.service.domain.ports.output.message.publisher.payment;

import com.andrei.food.ordering.system.service.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
