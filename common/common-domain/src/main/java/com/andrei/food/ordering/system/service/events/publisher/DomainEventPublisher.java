package com.andrei.food.ordering.system.service.events.publisher;

import com.andrei.food.ordering.system.service.events.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {
    void publish(T domainEvent);
}
