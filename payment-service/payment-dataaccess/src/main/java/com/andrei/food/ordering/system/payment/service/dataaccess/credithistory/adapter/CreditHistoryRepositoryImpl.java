package com.andrei.food.ordering.system.payment.service.dataaccess.credithistory.adapter;

import com.andrei.food.ordering.system.domain.entity.CreditHistory;
import com.andrei.food.ordering.system.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import com.andrei.food.ordering.system.payment.service.dataaccess.credithistory.mapper.CreditHistoryDataAccessMapper;
import com.andrei.food.ordering.system.payment.service.dataaccess.credithistory.repository.CreditHistoryJpaRepository;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository creditHistoryJpaRepository;
    private final CreditHistoryDataAccessMapper creditHistoryDataAccessMapper;

    @Override
    public CreditHistory save(CreditHistory creditHistory) {
        return creditHistoryDataAccessMapper.creditHistoryEntityToCreditHistory(creditHistoryJpaRepository
                .save(creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory)));
    }

    @Override
    public Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId) {
        Optional<List<CreditHistoryEntity>> creditHistory =
                creditHistoryJpaRepository.findByCustomerId(customerId.getValue());
        return creditHistory
                .map(creditHistoryList ->
                        creditHistoryList.stream()
                                .map(creditHistoryDataAccessMapper::creditHistoryEntityToCreditHistory)
                                .collect(Collectors.toList()));
    }
}
