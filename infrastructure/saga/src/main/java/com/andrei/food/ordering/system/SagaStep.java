package com.andrei.food.ordering.system;

public interface SagaStep<T> {
    void process(T data);

    void rollback(T data);
}
