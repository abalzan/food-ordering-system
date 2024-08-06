package com.andrei.food.ordering.system.domain.valueobject;

import com.andrei.food.ordering.system.service.valueobject.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {

    public PaymentId(UUID value) {
        super(value);
    }
}
