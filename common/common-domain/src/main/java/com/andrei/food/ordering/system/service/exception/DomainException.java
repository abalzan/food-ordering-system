package com.andrei.food.ordering.system.service.exception;

public class DomainException extends RuntimeException{

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
