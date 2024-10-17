package com.andrei.food.ordering.system.customer.service.domain;

import com.andrei.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.andrei.food.ordering.system.customer.service.domain.create.CreateCustomerResponse;
import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.andrei.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper;
import com.andrei.food.ordering.system.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CustomerApplicationServiceImplTest {

    private CustomerApplicationServiceImpl customerApplicationServiceImpl;
    private CustomerCreateCommandHandler customerCreateCommandHandler;
    private CustomerDataMapper customerDataMapper;
    private CustomerMessagePublisher customerMessagePublisher;

    @BeforeEach
    void setUp() {
        customerCreateCommandHandler = Mockito.mock(CustomerCreateCommandHandler.class);
        customerDataMapper = Mockito.mock(CustomerDataMapper.class);
        customerMessagePublisher = Mockito.mock(CustomerMessagePublisher.class);
        customerApplicationServiceImpl = new CustomerApplicationServiceImpl(customerCreateCommandHandler, customerDataMapper, customerMessagePublisher);
    }

    @Test
    void createCustomerSuccessfully() {
        CreateCustomerCommand command = new CreateCustomerCommand(UUID.randomUUID(), "firstName", "lastname", "username");
        CustomerCreatedEvent event = new CustomerCreatedEvent(new Customer(new CustomerId(UUID.randomUUID()), "username", "firstName", "lastName"), ZonedDateTime.now());
        CreateCustomerResponse response = new CreateCustomerResponse("customerId", "Customer saved successfully!");

        when(customerCreateCommandHandler.createCustomer(any(CreateCustomerCommand.class))).thenReturn(event);
        when(customerDataMapper.customerToCreateCustomerResponse(any(Customer.class), any(String.class))).thenReturn(response);

        CreateCustomerResponse result = customerApplicationServiceImpl.createCustomer(command);

        assertEquals(response, result);
    }

    @Test
    void createCustomerWithException() {
        CreateCustomerCommand command = new CreateCustomerCommand(UUID.randomUUID(), "firstName", "lastname", "username");

        when(customerCreateCommandHandler.createCustomer(any(CreateCustomerCommand.class)))
                .thenThrow(new RuntimeException("Error creating customer"));

        try {
            customerApplicationServiceImpl.createCustomer(command);
        } catch (RuntimeException e) {
            assertEquals("Error creating customer", e.getMessage());
        }
    }
}