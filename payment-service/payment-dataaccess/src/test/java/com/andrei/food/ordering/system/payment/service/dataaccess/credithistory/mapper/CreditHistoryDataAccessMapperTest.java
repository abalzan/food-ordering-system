package com.andrei.food.ordering.system.payment.service.dataaccess.credithistory.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.andrei.food.ordering.system.domain.entity.CreditHistory;
import com.andrei.food.ordering.system.domain.valueobject.CreditHistoryId;
import com.andrei.food.ordering.system.domain.valueobject.TransactionType;
import com.andrei.food.ordering.system.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.service.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

class CreditHistoryDataAccessMapperTest {

    private final CreditHistoryDataAccessMapper creditHistoryDataAccessMapper = new CreditHistoryDataAccessMapper();

    @Test
    void creditHistoryEntityToCreditHistoryMapsCorrectly() {
        CreditHistoryEntity creditHistoryEntity = CreditHistoryEntity.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.DEBIT)
                .build();

        CreditHistory creditHistory = creditHistoryDataAccessMapper.creditHistoryEntityToCreditHistory(creditHistoryEntity);

        assertNotNull(creditHistory);
        assertEquals(creditHistoryEntity.getId(), creditHistory.getId().getValue());
        assertEquals(creditHistoryEntity.getCustomerId(), creditHistory.getCustomerId().getValue());
        assertEquals(creditHistoryEntity.getAmount(), creditHistory.getAmount().getAmount());
        assertEquals(creditHistoryEntity.getType(), creditHistory.getTransactionType());
    }

    @Test
    void creditHistoryToCreditHistoryEntityMapsCorrectly() {
        CreditHistory creditHistory = CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .amount(new Money(new BigDecimal("100.00")))
                .transactionType(TransactionType.DEBIT)
                .build();

        CreditHistoryEntity creditHistoryEntity = creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory);

        assertNotNull(creditHistoryEntity);
        assertEquals(creditHistory.getId().getValue(), creditHistoryEntity.getId());
        assertEquals(creditHistory.getCustomerId().getValue(), creditHistoryEntity.getCustomerId());
        assertEquals(creditHistory.getAmount().getAmount(), creditHistoryEntity.getAmount());
        assertEquals(creditHistory.getTransactionType(), creditHistoryEntity.getType());
    }

    @Test
    void creditHistoryEntityToCreditHistoryHandlesNullValues() {
        CreditHistoryEntity creditHistoryEntity = CreditHistoryEntity.builder()
                .id(null)
                .customerId(null)
                .amount(null)
                .type(null)
                .build();

        CreditHistory creditHistory = creditHistoryDataAccessMapper.creditHistoryEntityToCreditHistory(creditHistoryEntity);

        assertNotNull(creditHistory);
        assertNull(creditHistory.getId().getValue());
        assertNull(creditHistory.getCustomerId().getValue());
        assertNull(creditHistory.getAmount().getAmount());
        assertNull(creditHistory.getTransactionType());
    }

    @Test
    void creditHistoryToCreditHistoryEntityHandlesNullValues() {
        CreditHistory creditHistory = CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(null))
                .customerId(new CustomerId(null))
                .amount(new Money(null))
                .transactionType(null)
                .build();

        CreditHistoryEntity creditHistoryEntity = creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory);

        assertNotNull(creditHistoryEntity);
        assertNull(creditHistoryEntity.getId());
        assertNull(creditHistoryEntity.getCustomerId());
        assertNull(creditHistoryEntity.getAmount());
        assertNull(creditHistoryEntity.getType());
    }
}