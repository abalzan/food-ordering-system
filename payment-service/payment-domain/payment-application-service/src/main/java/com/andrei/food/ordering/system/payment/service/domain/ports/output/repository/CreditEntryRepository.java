package com.andrei.food.ordering.system.payment.service.domain.ports.output.repository;

import com.andrei.food.ordering.system.domain.entity.CreditEntry;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;

import java.util.Optional;

public interface CreditEntryRepository {

    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}
