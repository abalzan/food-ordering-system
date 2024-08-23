package com.andrei.food.ordering.system.restaurant.service.domain.event;

import com.andrei.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import com.andrei.food.ordering.system.service.events.DomainEvent;
import com.andrei.food.ordering.system.service.valueobject.RestaurantId;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
public abstract class OrderApprovalEvent implements DomainEvent<OrderApproval> {

    private final OrderApproval orderApproval;
    private final RestaurantId restaurantId;
    private final List<String> failureMessages;
    private final ZonedDateTime createdAt;

    public OrderApprovalEvent(OrderApproval orderApproval, RestaurantId restaurantId, List<String> failureMessages, ZonedDateTime createdAt) {
        this.orderApproval = orderApproval;
        this.restaurantId = restaurantId;
        this.failureMessages = failureMessages;
        this.createdAt = createdAt;
    }
}
