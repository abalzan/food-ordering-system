package com.andrei.food.ordering.system.service.entity;

import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import lombok.Getter;

@Getter
public class Customer extends AggregateRoot<CustomerId> {

    private String userName;
    private String firstName;
    private String lastName;

    public Customer(CustomerId customerId, String userName, String firstName, String lastName) {
        super.setId(customerId);
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }
}
