package com.andrei.food.ordering.system.customer.service.domain.mapper;

import com.andrei.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.andrei.food.ordering.system.customer.service.domain.create.CreateCustomerResponse;
import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomerDataMapperTest {

    private CustomerDataMapper customerDataMapper;

    @BeforeEach
    void setUp() {
        customerDataMapper = new CustomerDataMapper();
    }

    @Test
    void createCustomerCommandToCustomerSuccessfully() {
        UUID customerId = UUID.randomUUID();
        CreateCustomerCommand command = new CreateCustomerCommand(customerId, "John", "Doe", "john_doe");

        Customer customer = customerDataMapper.createCustomerCommandToCustomer(command);

        assertEquals(customerId, customer.getId().getValue());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("john_doe", customer.getUsername());
    }

    @Test
    void createCustomerCommandToCustomerWithNullValues() {
        CreateCustomerCommand command = new CreateCustomerCommand(null, null, null, null);

        Customer customer = customerDataMapper.createCustomerCommandToCustomer(command);

        assertNull(customer.getId().getValue());
        assertNull(customer.getFirstName());
        assertNull(customer.getLastName());
        assertNull(customer.getUsername());
    }

    @Test
    void customerToCreateCustomerResponseSuccessfully() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(new CustomerId(customerId), "john_doe", "John", "Doe");

        CreateCustomerResponse response = customerDataMapper.customerToCreateCustomerResponse(customer, "Customer created successfully");

        assertEquals(customerId.toString(), response.customerId());
        assertEquals("Customer created successfully", response.message());
    }

}