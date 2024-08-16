package com.andrei.food.ordering.system.payment.service.dataaccess.credithistory.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.domain.entity.CreditHistory;
import com.andrei.food.ordering.system.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import com.andrei.food.ordering.system.payment.service.dataaccess.credithistory.mapper.CreditHistoryDataAccessMapper;
import com.andrei.food.ordering.system.payment.service.dataaccess.credithistory.repository.CreditHistoryJpaRepository;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CreditHistoryRepositoryImplTest {

    @Mock
    private CreditHistoryDataAccessMapper creditHistoryDataAccessMapper;

    @Mock
    private CreditHistoryJpaRepository creditHistoryJpaRepository;

    @InjectMocks
    private CreditHistoryRepositoryImpl creditHistoryRepositoryImpl;

    @Test
    void saveCreditHistorySuccessfully() {
        CreditHistory creditHistory = CreditHistory.builder().build();
        CreditHistoryEntity creditHistoryEntity = new CreditHistoryEntity();

        when(creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory)).thenReturn(creditHistoryEntity);
        when(creditHistoryJpaRepository.save(creditHistoryEntity)).thenReturn(creditHistoryEntity);
        when(creditHistoryDataAccessMapper.creditHistoryEntityToCreditHistory(creditHistoryEntity)).thenReturn(creditHistory);

        CreditHistory result = creditHistoryRepositoryImpl.save(creditHistory);

        assertNotNull(result);
        assertEquals(creditHistory, result);
    }

    @Test
    void findByCustomerIdReturnsCreditHistoryList() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());
        CreditHistoryEntity creditHistoryEntity = new CreditHistoryEntity();
        CreditHistory creditHistory = CreditHistory.builder().build();
        List<CreditHistoryEntity> creditHistoryEntities = List.of(creditHistoryEntity);
        List<CreditHistory> creditHistories = List.of(creditHistory);

        when(creditHistoryJpaRepository.findByCustomerId(customerId.getValue())).thenReturn(Optional.of(creditHistoryEntities));
        when(creditHistoryDataAccessMapper.creditHistoryEntityToCreditHistory(creditHistoryEntity)).thenReturn(creditHistory);

        Optional<List<CreditHistory>> result = creditHistoryRepositoryImpl.findByCustomerId(customerId);

        assertTrue(result.isPresent());
        assertEquals(creditHistories, result.get());
    }

    @Test
    void findByCustomerIdReturnsEmptyWhenNotFound() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());

        when(creditHistoryJpaRepository.findByCustomerId(customerId.getValue())).thenReturn(Optional.empty());

        Optional<List<CreditHistory>> result = creditHistoryRepositoryImpl.findByCustomerId(customerId);

        assertFalse(result.isPresent());
    }
}