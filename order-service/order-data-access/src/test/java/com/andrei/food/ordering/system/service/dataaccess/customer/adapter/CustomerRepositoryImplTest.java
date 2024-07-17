package com.andrei.food.ordering.system.service.dataaccess.customer.adapter;

import com.andrei.food.ordering.system.service.entity.Customer;
import com.andrei.food.ordering.system.service.dataaccess.customer.repository.CustomerJpaRepository;
import com.andrei.food.ordering.system.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
