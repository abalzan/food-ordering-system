package com.andrei.food.ordering.system.service.events;

public interface DomainEvent<T> {
    void fire();
}
