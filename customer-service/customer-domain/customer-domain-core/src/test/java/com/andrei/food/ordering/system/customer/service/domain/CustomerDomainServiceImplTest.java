package com.andrei.food.ordering.system.customer.service.domain;

import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerDomainServiceImplTest {

    private CustomerDomainServiceImpl customerDomainServiceImpl;

    @BeforeEach
    void setUp() {
        customerDomainServiceImpl = new CustomerDomainServiceImpl();
    }

    @Test
    void validateAndInitiateCustomerSuccessfully() {
        Customer customer = new Customer(new CustomerId(UUID.randomUUID()), "john_doe", "John", "Doe");

        CustomerCreatedEvent event = customerDomainServiceImpl.validateAndInitiateCustomer(customer);

        assertEquals(customer, event.getCustomer());
    }
}