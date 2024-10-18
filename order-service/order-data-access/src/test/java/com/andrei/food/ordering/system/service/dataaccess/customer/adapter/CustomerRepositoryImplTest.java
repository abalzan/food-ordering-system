package com.andrei.food.ordering.system.service.dataaccess.customer.adapter;

import com.andrei.food.ordering.system.service.dataaccess.customer.entity.CustomerEntity;
import com.andrei.food.ordering.system.service.entity.Customer;
import com.andrei.food.ordering.system.service.dataaccess.customer.repository.CustomerJpaRepository;
import com.andrei.food.ordering.system.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class CustomerRepositoryImplTest {

    @Mock
    private CustomerJpaRepository customerJpaRepository;

    @Mock
    private CustomerDataAccessMapper customerDataAccessMapper;

    private CustomerRepositoryImpl customerRepositoryImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerRepositoryImpl = new CustomerRepositoryImpl(customerJpaRepository, customerDataAccessMapper);
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        when(customerJpaRepository.findById(customerId)).thenReturn(Optional.empty());

        Optional<Customer> foundCustomer = customerRepositoryImpl.findCustomer(customerId);

        assertEquals(Optional.empty(), foundCustomer);
    }

    @Test
    void shouldSaveCustomerSuccessfully() {
        Customer customer = new Customer(new CustomerId(UUID.randomUUID()), "john_doe", "John", "Doe");
        CustomerEntity customerEntity = new CustomerEntity();

        when(customerDataAccessMapper.customerToCustomerEntity(customer)).thenReturn(customerEntity);
        when(customerJpaRepository.save(customerEntity)).thenReturn(customerEntity);
        when(customerDataAccessMapper.customerEntityToCustomer(customerEntity)).thenReturn(customer);

        Customer savedCustomer = customerRepositoryImpl.save(customer);

        assertEquals(customer, savedCustomer);
    }

    @Test
    void shouldHandleExceptionDuringSave() {
        Customer customer = new Customer(new CustomerId(UUID.randomUUID()), "john_doe", "John", "Doe");
        CustomerEntity customerEntity = new CustomerEntity();

        when(customerDataAccessMapper.customerToCustomerEntity(customer)).thenReturn(customerEntity);
        when(customerJpaRepository.save(customerEntity)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> customerRepositoryImpl.save(customer));
    }
}
