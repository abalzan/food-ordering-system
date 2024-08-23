package com.andrei.food.ordering.system.restaurant.service.domain.valueobject;

import com.andrei.food.ordering.system.service.valueobject.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {

    public OrderApprovalId(UUID value) {
        super(value);
    }
}
