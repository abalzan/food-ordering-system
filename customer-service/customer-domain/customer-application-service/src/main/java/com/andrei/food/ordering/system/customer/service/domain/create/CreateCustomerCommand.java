package com.andrei.food.ordering.system.customer.service.domain.create;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCustomerCommand(@NotNull UUID customerId, @NotNull String firstName, @NotNull String lastName,
                                    @NotNull String username) {
}
