package com.andrei.food.ordering.system.customer.service.dataaccess.adapter;

import com.andrei.food.ordering.system.customer.service.dataaccess.entity.CustomerEntity;
import com.andrei.food.ordering.system.customer.service.dataaccess.mapper.CustomerDataAccessMapper;
import com.andrei.food.ordering.system.customer.service.dataaccess.repository.CustomerJpaRepository;
import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CustomerRepositoryImplTest {

    private CustomerRepositoryImpl customerRepositoryImpl;
    private CustomerJpaRepository customerJpaRepository;
    private CustomerDataAccessMapper customerDataAccessMapper;

    @BeforeEach
    void setUp() {
        customerJpaRepository = Mockito.mock(CustomerJpaRepository.class);
        customerDataAccessMapper = Mockito.mock(CustomerDataAccessMapper.class);
        customerRepositoryImpl = new CustomerRepositoryImpl(customerJpaRepository, customerDataAccessMapper);
    }

    @Test
    void createCustomerSuccessfully() {
        Customer customer = new Customer(new CustomerId(UUID.randomUUID()), "username", "firstName", "lastName");
        CustomerEntity customerEntity = new CustomerEntity();
        when(customerDataAccessMapper.customerToCustomerEntity(any(Customer.class))).thenReturn(customerEntity);
        when(customerJpaRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);
        when(customerDataAccessMapper.customerEntityToCustomer(any(CustomerEntity.class))).thenReturn(customer);

        Customer result = customerRepositoryImpl.createCustomer(customer);

        assertEquals(customer, result);
    }

    @Test
    void createCustomerWithNullCustomer() {
        Customer result = customerRepositoryImpl.createCustomer(null);

        assertEquals(null, result);
    }
}