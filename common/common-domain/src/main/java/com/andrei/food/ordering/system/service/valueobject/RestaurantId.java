package com.andrei.food.ordering.system.service.valueobject;

import java.util.UUID;

public class RestaurantId extends BaseId<UUID>{
    public RestaurantId(UUID value) {
        super(value);
    }
}
