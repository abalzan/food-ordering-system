package com.andrei.food.ordering.system.customer.service.dataaccess.mapper;

import com.andrei.food.ordering.system.customer.service.dataaccess.entity.CustomerEntity;
import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerDataAccessMapperTest {

    private CustomerDataAccessMapper customerDataAccessMapper;

    @BeforeEach
    void setUp() {
        customerDataAccessMapper = new CustomerDataAccessMapper();
    }

    @Test
    void customerEntityToCustomerSuccessfully() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customerEntity = CustomerEntity.builder()
                .id(customerId)
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .build();

        Customer customer = customerDataAccessMapper.customerEntityToCustomer(customerEntity);

        assertEquals(customerId, customer.getId().getValue());
        assertEquals("john_doe", customer.getUsername());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
    }

    @Test
    void customerToCustomerEntitySuccessfully() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(new CustomerId(customerId), "john_doe", "John", "Doe");

        CustomerEntity customerEntity = customerDataAccessMapper.customerToCustomerEntity(customer);

        assertEquals(customerId, customerEntity.getId());
        assertEquals("john_doe", customerEntity.getUsername());
        assertEquals("John", customerEntity.getFirstName());
        assertEquals("Doe", customerEntity.getLastName());
    }

    @Test
    void customerEntityToCustomerWithNullValues() {
        CustomerEntity customerEntity = CustomerEntity.builder()
                .id(null)
                .username(null)
                .firstName(null)
                .lastName(null)
                .build();

        Customer customer = customerDataAccessMapper.customerEntityToCustomer(customerEntity);

        assertEquals(null, customer.getId().getValue());
        assertEquals(null, customer.getUsername());
        assertEquals(null, customer.getFirstName());
        assertEquals(null, customer.getLastName());
    }

    @Test
    void customerToCustomerEntityWithNullValues() {
        Customer customer = new Customer(new CustomerId(null), null, null, null);

        CustomerEntity customerEntity = customerDataAccessMapper.customerToCustomerEntity(customer);

        assertEquals(null, customerEntity.getId());
        assertEquals(null, customerEntity.getUsername());
        assertEquals(null, customerEntity.getFirstName());
        assertEquals(null, customerEntity.getLastName());
    }
}