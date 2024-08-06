package com.andrei.food.ordering.system.domain.valueobject;

import com.andrei.food.ordering.system.service.valueobject.BaseId;

import java.util.UUID;

public class CreditEntryId extends BaseId<UUID> {
    public CreditEntryId(UUID value) {
        super(value);
    }
}
