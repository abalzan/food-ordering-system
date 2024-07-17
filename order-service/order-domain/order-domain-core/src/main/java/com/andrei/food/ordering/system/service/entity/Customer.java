package com.andrei.food.ordering.system.service.entity;

import com.andrei.food.ordering.system.service.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {

    public Customer() {
    }

    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }
}
