package com.andrei.food.ordering.system.application.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;

public class ConstraintViolationMock implements ConstraintViolation<Object> {

    private final String message;

    public ConstraintViolationMock(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    // Implement other methods from the ConstraintViolation interface
    // You can return default values or mock values as per your test requirements

    @Override
    public String getMessageTemplate() {
        return null;
    }

    @Override
    public Object getRootBean() {
        return null;
    }

    @Override
    public Class<Object> getRootBeanClass() {
        return null;
    }

    @Override
    public Object getLeafBean() {
        return null;
    }

    @Override
    public Object[] getExecutableParameters() {
        return new Object[0];
    }

    @Override
    public Object getExecutableReturnValue() {
        return null;
    }

    @Override
    public Path getPropertyPath() {
        return null;
    }

    @Override
    public Object getInvalidValue() {
        return null;
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return null;
    }

    @Override
    public <U> U unwrap(Class<U> type) {
        return null;
    }
}
