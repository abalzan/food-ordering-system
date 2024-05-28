package com.andrei.food.ordering.service.domain.dto.create;

import com.andrei.food.ordering.system.domain.valueobject.ProductId;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItem(@NotNull UUID productId, @NotNull Integer quantity, @NotNull BigDecimal price,
                        @NotNull BigDecimal subTotal) {
}
