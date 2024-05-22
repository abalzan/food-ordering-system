package com.andrei.food.ordering.service.domain.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public record OrderAddress(@NotNull @Max(value = 50) String street, @NotNull @Max(value = 50) String city,
                           @NotNull @Max(value = 10) String postalCode) {
}
