package com.andrei.food.ordering.system.payment.service.domain.exception;

import com.andrei.food.ordering.system.service.exception.DomainException;

public class PaymentApplicationServiceException extends DomainException {
    public PaymentApplicationServiceException(String message) {
        super(message);
    }

    public PaymentApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
