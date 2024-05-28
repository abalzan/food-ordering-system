package com.andrei.food.ordering.service.domain.dto.track;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
public record TrackOrderQuery(@NotNull UUID orderTrackingId) {
}
