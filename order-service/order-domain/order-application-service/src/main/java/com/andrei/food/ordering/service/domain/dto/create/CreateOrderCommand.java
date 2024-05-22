package com.andrei.food.ordering.service.domain.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public record CreateOrderCommand(UUID customerId, @NotNull UUID restaurantId, @NotNull BigDecimal price,
                                 @NotNull List<OrderItem> items, @NotNull OrderAddress address) {

}
