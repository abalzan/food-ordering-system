package com.andrei.food.ordering.service.domain.dto.message;

import com.andrei.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantApprovalResponse {
    private String id;
    private String sagaId;
    private String orderId;
    private String restaurantId;
    private Instant createAt;
    private OrderApprovalStatus orderApprovalStatus;
    private List<String> failureMessages;

}
