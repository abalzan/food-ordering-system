package com.andrei.food.ordering.system.service.valueobject;

import java.util.UUID;

public class OrderId extends BaseId<UUID>{
    public OrderId(UUID value) {
        super(value);
    }
}
