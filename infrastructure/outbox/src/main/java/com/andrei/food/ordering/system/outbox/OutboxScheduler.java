package com.andrei.food.ordering.system.outbox;

public interface OutboxScheduler {

    void processOutboxMessage();
}
