package com.andrei.food.ordering.system;

import com.andrei.food.ordering.system.service.events.DomainEvent;

public interface SagaStep<T> {
    void process(T data);

    void rollback(T data);
}
