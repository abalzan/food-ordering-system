package com.andrei.food.ordering.system.customer.service.domain.create;

import jakarta.validation.constraints.NotNull;

public record CreateCustomerResponse(@NotNull String customerId, @NotNull String message) {
}
