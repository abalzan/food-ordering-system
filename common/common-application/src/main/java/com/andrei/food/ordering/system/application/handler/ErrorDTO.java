package com.andrei.food.ordering.system.application.handler;

import lombok.Builder;

@Builder
public record ErrorDTO(String message, String code) {
}
