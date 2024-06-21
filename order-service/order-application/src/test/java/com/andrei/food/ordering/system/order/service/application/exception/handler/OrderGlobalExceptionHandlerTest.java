package com.andrei.food.ordering.system.order.service.application.exception.handler;

import com.andrei.food.ordering.system.application.handler.ErrorDTO;
import com.andrei.food.ordering.system.domain.exception.OrderDomainException;
import com.andrei.food.ordering.system.domain.exception.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class OrderGlobalExceptionHandlerTest {

    @Mock
    private OrderDomainException orderDomainException;

    @Mock
    private OrderNotFoundException orderNotFoundException;

    private OrderGlobalExceptionHandler orderGlobalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderGlobalExceptionHandler = new OrderGlobalExceptionHandler();
    }

    @Test
    void shouldHandleOrderDomainException() {
        when(orderDomainException.getMessage()).thenReturn("Order domain error");

        ErrorDTO errorDTO = orderGlobalExceptionHandler.handleException(orderDomainException);

        assertEquals("Order domain error", errorDTO.message());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorDTO.code());
    }

    @Test
    void shouldHandleOrderNotFoundException() {
        when(orderNotFoundException.getMessage()).thenReturn("Order not found");

        ErrorDTO errorDTO = orderGlobalExceptionHandler.handleException(orderNotFoundException);

        assertEquals("Order not found", errorDTO.message());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorDTO.code());
    }
}
