package com.andrei.food.ordering.service.domain.dto.track;

import com.andrei.food.ordering.system.domain.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public record TrackOrderResponse(@NotNull UUID orderTrackingId, @NotNull OrderStatus orderStatus,
                                 List<String> failureMessages) {
}
