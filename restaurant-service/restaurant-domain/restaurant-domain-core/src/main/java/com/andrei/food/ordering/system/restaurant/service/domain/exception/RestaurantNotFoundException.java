package com.andrei.food.ordering.system.restaurant.service.domain.exception;

import com.andrei.food.ordering.system.service.exception.DomainException;

public class RestaurantNotFoundException extends DomainException {
    public RestaurantNotFoundException(String message) {
        super(message);
    }

    public RestaurantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
