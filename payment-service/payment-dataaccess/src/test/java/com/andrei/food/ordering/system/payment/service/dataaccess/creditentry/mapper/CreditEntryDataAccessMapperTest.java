package com.andrei.food.ordering.system.payment.service.dataaccess.creditentry.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.andrei.food.ordering.system.domain.entity.CreditEntry;
import com.andrei.food.ordering.system.domain.valueobject.CreditEntryId;
import com.andrei.food.ordering.system.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.service.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

class CreditEntryDataAccessMapperTest {

    private final CreditEntryDataAccessMapper creditEntryDataAccessMapper = new CreditEntryDataAccessMapper();

    @Test
    void creditEntryEntityToCreditEntryMapsCorrectly() {
        CreditEntryEntity creditEntryEntity = CreditEntryEntity.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .totalCreditAmount(new BigDecimal("100.00"))
                .build();

        CreditEntry creditEntry = creditEntryDataAccessMapper.creditEntryEntityToCreditEntry(creditEntryEntity);

        assertNotNull(creditEntry);
        assertEquals(creditEntryEntity.getId(), creditEntry.getId().getValue());
        assertEquals(creditEntryEntity.getCustomerId(), creditEntry.getCustomerId().getValue());
        assertEquals(creditEntryEntity.getTotalCreditAmount(), creditEntry.getTotalCreditAmount().getAmount());
    }

    @Test
    void creditEntryToCreditEntryEntityMapsCorrectly() {
        CreditEntry creditEntry = CreditEntry.builder()
                .creditEntryId(new CreditEntryId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .totalCreditAmount(new Money(new BigDecimal("100.00")))
                .build();

        CreditEntryEntity creditEntryEntity = creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry);

        assertNotNull(creditEntryEntity);
        assertEquals(creditEntry.getId().getValue(), creditEntryEntity.getId());
        assertEquals(creditEntry.getCustomerId().getValue(), creditEntryEntity.getCustomerId());
        assertEquals(creditEntry.getTotalCreditAmount().getAmount(), creditEntryEntity.getTotalCreditAmount());
    }
}