package com.andrei.food.ordering.system.payment.service.dataaccess.creditentry.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.domain.entity.CreditEntry;
import com.andrei.food.ordering.system.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import com.andrei.food.ordering.system.payment.service.dataaccess.creditentry.mapper.CreditEntryDataAccessMapper;
import com.andrei.food.ordering.system.payment.service.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CreditEntryRepositoryImplTest {

    @Mock
    private CreditEntryDataAccessMapper creditEntryDataAccessMapper;

    @Mock
    private CreditEntryJpaRepository creditEntryJpaRepository;

    @InjectMocks
    private CreditEntryRepositoryImpl creditEntryRepositoryImpl;

    @Test
    void saveCreditEntrySuccessfully() {
        CreditEntry creditEntry = CreditEntry.builder().build();
        CreditEntryEntity creditEntryEntity = new CreditEntryEntity();

        when(creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry)).thenReturn(creditEntryEntity);
        when(creditEntryJpaRepository.save(creditEntryEntity)).thenReturn(creditEntryEntity);
        when(creditEntryDataAccessMapper.creditEntryEntityToCreditEntry(creditEntryEntity)).thenReturn(creditEntry);

        CreditEntry result = creditEntryRepositoryImpl.save(creditEntry);

        assertNotNull(result);
        assertEquals(creditEntry, result);
    }

    @Test
    void findByCustomerIdReturnsCreditEntry() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());
        CreditEntryEntity creditEntryEntity = new CreditEntryEntity();
        CreditEntry creditEntry = CreditEntry.builder().build();

        when(creditEntryJpaRepository.findByCustomerId(customerId.getValue())).thenReturn(Optional.of(creditEntryEntity));
        when(creditEntryDataAccessMapper.creditEntryEntityToCreditEntry(creditEntryEntity)).thenReturn(creditEntry);

        Optional<CreditEntry> result = creditEntryRepositoryImpl.findByCustomerId(customerId);

        assertTrue(result.isPresent());
        assertEquals(creditEntry, result.get());
    }

    @Test
    void findByCustomerIdReturnsEmptyWhenNotFound() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());

        when(creditEntryJpaRepository.findByCustomerId(customerId.getValue())).thenReturn(Optional.empty());

        Optional<CreditEntry> result = creditEntryRepositoryImpl.findByCustomerId(customerId);

        assertFalse(result.isPresent());
    }
}