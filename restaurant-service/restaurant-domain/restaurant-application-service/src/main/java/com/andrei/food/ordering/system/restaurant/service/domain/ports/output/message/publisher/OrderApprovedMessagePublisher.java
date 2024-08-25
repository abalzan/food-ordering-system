package com.andrei.food.ordering.system.restaurant.service.domain.ports.output.message.publisher;

import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;

public interface OrderApprovedMessagePublisher extends DomainEventPublisher<OrderApprovedEvent> {
}
