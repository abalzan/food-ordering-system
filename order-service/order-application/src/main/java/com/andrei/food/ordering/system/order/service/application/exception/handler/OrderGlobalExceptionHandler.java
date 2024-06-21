package com.andrei.food.ordering.system.order.service.application.exception.handler;

import com.andrei.food.ordering.system.application.handler.ErrorDTO;
import com.andrei.food.ordering.system.application.handler.GlobalExceptionHandler;
import com.andrei.food.ordering.system.domain.exception.OrderDomainException;
import com.andrei.food.ordering.system.domain.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class OrderGlobalExceptionHandler extends GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(OrderDomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(OrderDomainException orderDomainException) {
        log.error("OrderDomainException: {}", orderDomainException.getMessage(), orderDomainException);
        return ErrorDTO.builder()
                .message(orderDomainException.getMessage())
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(OrderDomainException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleException(OrderNotFoundException orderNotFoundException) {
        log.error("OrderNotFoundException: {}", orderNotFoundException.getMessage(), orderNotFoundException);
        return ErrorDTO.builder()
                .message(orderNotFoundException.getMessage())
                .code(HttpStatus.NOT_FOUND.getReasonPhrase())
                .build();
    }
}
