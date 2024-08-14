package com.andrei.food.ordering.system.payment.service.domain.ports.output.repository;

import com.andrei.food.ordering.system.domain.entity.CreditHistory;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {
    CreditHistory save(CreditHistory creditHistory);

    Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId);
}
