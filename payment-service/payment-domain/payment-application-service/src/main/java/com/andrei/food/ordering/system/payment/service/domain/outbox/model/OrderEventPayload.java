package com.andrei.food.ordering.system.payment.service.domain.outbox.model;


import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.valueobject.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderEventPayload {
    @JsonProperty
    private String paymentId;
    @JsonProperty
    private String customerId;
    @JsonProperty
    private String orderId;
    @JsonProperty
    private BigDecimal price;
    @JsonProperty
    private ZonedDateTime createdAt;
    @JsonProperty
    private String paymentStatus;
    @JsonProperty
    private List<String> failureMessages;
}
