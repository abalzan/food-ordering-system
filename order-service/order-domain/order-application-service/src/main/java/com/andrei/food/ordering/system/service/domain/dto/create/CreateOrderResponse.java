package com.andrei.food.ordering.system.service.domain.dto.create;

import com.andrei.food.ordering.system.service.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

public record CreateOrderResponse(@NotNull UUID orderTrackingId, @NotNull OrderStatus orderStatus,
                                  @NotNull String message) {
    @Builder public CreateOrderResponse {}
}
