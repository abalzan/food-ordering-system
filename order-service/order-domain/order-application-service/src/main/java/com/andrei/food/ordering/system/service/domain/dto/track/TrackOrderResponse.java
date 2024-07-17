package com.andrei.food.ordering.system.service.domain.dto.track;

import com.andrei.food.ordering.system.service.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public record TrackOrderResponse(@NotNull UUID orderTrackingId, @NotNull OrderStatus orderStatus,
                                 List<String> failureMessages) {
    @Builder public TrackOrderResponse {}
}