package com.andrei.food.ordering.system.customer.service.application.rest;

import com.andrei.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.andrei.food.ordering.system.customer.service.domain.create.CreateCustomerResponse;
import com.andrei.food.ordering.system.customer.service.domain.ports.input.service.CustomerApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CustomerControllerTest {

    private CustomerController customerController;
    private CustomerApplicationService customerApplicationService;

    @BeforeEach
    void setUp() {
        customerApplicationService = Mockito.mock(CustomerApplicationService.class);
        customerController = new CustomerController(customerApplicationService);
    }

    @Test
    void createCustomerSuccessfully() {
        CreateCustomerCommand command = new CreateCustomerCommand(UUID.randomUUID(), "firstName", "surname", "username" );
        CreateCustomerResponse response = new CreateCustomerResponse("customerId", "username");
        when(customerApplicationService.createCustomer(any(CreateCustomerCommand.class))).thenReturn(response);

        ResponseEntity<CreateCustomerResponse> result = customerController.createCustomer(command);

        assertEquals(ResponseEntity.ok(response), result);
    }
}