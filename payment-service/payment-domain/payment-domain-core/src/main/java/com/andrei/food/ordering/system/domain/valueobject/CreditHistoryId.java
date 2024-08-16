package com.andrei.food.ordering.system.domain.valueobject;

import com.andrei.food.ordering.system.service.valueobject.BaseId;

import java.util.UUID;

public class CreditHistoryId extends BaseId<UUID> {
    public CreditHistoryId(UUID value) {
        super(value);
    }
}