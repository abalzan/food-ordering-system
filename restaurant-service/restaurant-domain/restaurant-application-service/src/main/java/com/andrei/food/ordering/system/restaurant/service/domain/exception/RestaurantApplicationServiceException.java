package com.andrei.food.ordering.system.restaurant.service.domain.exception;

import com.andrei.food.ordering.system.service.exception.DomainException;

public class RestaurantApplicationServiceException extends DomainException {
    public RestaurantApplicationServiceException(String message) {
        super(message);
    }

    public RestaurantApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
