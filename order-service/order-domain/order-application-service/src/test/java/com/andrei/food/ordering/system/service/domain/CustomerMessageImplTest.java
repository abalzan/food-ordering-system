package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.service.domain.dto.message.CustomerModel;
import com.andrei.food.ordering.system.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.system.service.domain.ports.output.repository.CustomerRepository;
import com.andrei.food.ordering.system.service.entity.Customer;
import com.andrei.food.ordering.system.service.exception.OrderDomainException;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerMessageImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderDataMapper orderDataMapper;

    @InjectMocks
    private CustomerMessageImpl customerMessageImpl;

    private CustomerModel customerModel;
    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UUID customerId = UUID.randomUUID();
        customerModel = new CustomerModel(customerId.toString(), "john_doe", "John", "Doe");
        customer = new Customer(new CustomerId(customerId), "john_doe", "John", "Doe");
    }

    @Test
    void customerCreatedSuccessfully() {
        when(orderDataMapper.customerModelToCustomer(any(CustomerModel.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        customerMessageImpl.customerCreated(customerModel);

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void customerCreationFails() {
        when(orderDataMapper.customerModelToCustomer(any(CustomerModel.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(null);

        assertThrows(OrderDomainException.class, () -> customerMessageImpl.customerCreated(customerModel));

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void customerModelToCustomerMappingFails() {
        when(orderDataMapper.customerModelToCustomer(any(CustomerModel.class))).thenThrow(new RuntimeException("Mapping error"));

        assertThrows(RuntimeException.class, () -> customerMessageImpl.customerCreated(customerModel));

        verify(customerRepository, never()).save(any(Customer.class));
    }
}