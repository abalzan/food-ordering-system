package com.andrei.food.ordering.system;

import com.andrei.food.ordering.system.service.events.DomainEvent;

public interface SagaStep<T, S extends DomainEvent, U extends DomainEvent> {
    S process(T data);

    U rollback(T data);
}
