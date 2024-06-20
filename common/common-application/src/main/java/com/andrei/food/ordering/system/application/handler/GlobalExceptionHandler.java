package com.andrei.food.ordering.system.application.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleException(Exception exception) {
        log.error("Exception: {}", exception.getMessage(), exception);
        return ErrorDTO.builder()
                .message("Unexpected Error")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(ValidationException exception) {
        ErrorDTO errorDTO;
        if(exception instanceof ValidationException) {
            String violations = extractViolationsFromException((ConstraintViolationException) exception);
            log.error("ValidationException: {}", violations, exception);
            errorDTO = ErrorDTO.builder()
                    .message(violations)
                    .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .build();
        } else {
            log.error("ValidationException: {}", exception.getMessage(), exception);
            errorDTO = ErrorDTO.builder()
                    .message(exception.getMessage())
                    .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .build();
        }

      return errorDTO;
    }

    private String extractViolationsFromException(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("--"));
    }
}
