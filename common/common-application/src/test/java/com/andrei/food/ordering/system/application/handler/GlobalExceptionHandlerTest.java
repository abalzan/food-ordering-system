package com.andrei.food.ordering.system.application.handler;
import com.andrei.food.ordering.system.application.handler.GlobalExceptionHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Mock
    private ConstraintViolationException constraintViolationException;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleException() {
        Exception exception = new Exception("Unexpected error");
        ErrorDTO errorDTO = globalExceptionHandler.handleException(exception);

        assertEquals("Unexpected Error", errorDTO.message());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), errorDTO.code());
    }

    @Test
    void shouldHandleValidationExceptionWithConstraintViolations() {
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        constraintViolations.add(new ConstraintViolationMock("Violation 1"));
        constraintViolations.add(new ConstraintViolationMock("Violation 2"));

        when(constraintViolationException.getConstraintViolations()).thenReturn(constraintViolations);

        ErrorDTO errorDTO = globalExceptionHandler.handleException(constraintViolationException);

        assertEquals("Violation 1--Violation 2", errorDTO.message());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorDTO.code());
    }

    @Test
    void shouldHandleExceptionWithoutConstraintViolations() {
        Exception exception = new Exception("blah");

        ErrorDTO errorDTO = globalExceptionHandler.handleException(exception);

        assertEquals("Unexpected Error", errorDTO.message());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), errorDTO.code());
    }
}
