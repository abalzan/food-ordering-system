package com.andrei.food.ordering.system.service.valueobject;

import java.util.UUID;

public class CustomerId extends BaseId<UUID>{
    public CustomerId(UUID value) {
        super(value);
    }
}
