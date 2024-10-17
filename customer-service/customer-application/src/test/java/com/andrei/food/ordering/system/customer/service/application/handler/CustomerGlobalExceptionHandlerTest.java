package com.andrei.food.ordering.system.customer.service.application.handler;

import com.andrei.food.ordering.system.application.handler.ErrorDTO;
import com.andrei.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerGlobalExceptionHandlerTest {

    private CustomerGlobalExceptionHandler customerGlobalExceptionHandler;

    @BeforeEach
    void setUp() {
        customerGlobalExceptionHandler = new CustomerGlobalExceptionHandler();
    }

    @Test
    void handleCustomerDomainException() {
        CustomerDomainException exception = new CustomerDomainException("Customer not found");
        ErrorDTO errorDTO = customerGlobalExceptionHandler.handleException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorDTO.code());
        assertEquals("Customer not found", errorDTO.message());
    }
}