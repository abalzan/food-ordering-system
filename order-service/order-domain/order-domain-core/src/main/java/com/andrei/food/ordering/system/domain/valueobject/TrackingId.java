package com.andrei.food.ordering.system.domain.valueobject;

import com.andrei.food.ordering.system.domain.domain.valueobject.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public TrackingId(UUID value) {
        super(value);
    }
}
