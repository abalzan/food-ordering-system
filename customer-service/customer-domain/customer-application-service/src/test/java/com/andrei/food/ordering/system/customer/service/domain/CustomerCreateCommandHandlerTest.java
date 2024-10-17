package com.andrei.food.ordering.system.customer.service.domain;

import com.andrei.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.andrei.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.andrei.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper;
import com.andrei.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CustomerCreateCommandHandlerTest {

    private CustomerCreateCommandHandler customerCreateCommandHandler;
    private CustomerDomainService customerDomainService;
    private CustomerRepository customerRepository;
    private CustomerDataMapper customerDataMapper;

    @BeforeEach
    void setUp() {
        customerDomainService = Mockito.mock(CustomerDomainService.class);
        customerRepository = Mockito.mock(CustomerRepository.class);
        customerDataMapper = Mockito.mock(CustomerDataMapper.class);
        customerCreateCommandHandler = new CustomerCreateCommandHandler(customerDomainService, customerRepository, customerDataMapper);
    }

    @Test
    void createCustomerSuccessfully() {
        CreateCustomerCommand command = new CreateCustomerCommand(UUID.randomUUID(), "John", "Doe", "john_doe");
        Customer customer = new Customer(new CustomerId(UUID.randomUUID()), "john_doe", "John", "Doe");
        CustomerCreatedEvent event = new CustomerCreatedEvent(customer, ZonedDateTime.now());

        when(customerDataMapper.createCustomerCommandToCustomer(any(CreateCustomerCommand.class))).thenReturn(customer);
        when(customerDomainService.validateAndInitiateCustomer(any(Customer.class))).thenReturn(event);
        when(customerRepository.createCustomer(any(Customer.class))).thenReturn(customer);

        CustomerCreatedEvent result = customerCreateCommandHandler.createCustomer(command);

        assertEquals(event, result);
    }

    @Test
    void createCustomerWithException() {
        CreateCustomerCommand command = new CreateCustomerCommand(UUID.randomUUID(), "John", "Doe", "john_doe");
        Customer customer = new Customer(new CustomerId(UUID.randomUUID()), "john_doe", "John", "Doe");

        when(customerDataMapper.createCustomerCommandToCustomer(any(CreateCustomerCommand.class))).thenReturn(customer);
        when(customerDomainService.validateAndInitiateCustomer(any(Customer.class))).thenReturn(new CustomerCreatedEvent(customer, ZonedDateTime.now()));
        when(customerRepository.createCustomer(any(Customer.class))).thenReturn(null);

        assertThrows(CustomerDomainException.class, () -> customerCreateCommandHandler.createCustomer(command));
    }
}