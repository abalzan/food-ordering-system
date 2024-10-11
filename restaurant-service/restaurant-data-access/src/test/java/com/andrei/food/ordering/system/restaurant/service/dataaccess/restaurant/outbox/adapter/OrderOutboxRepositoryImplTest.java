package com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.adapter;

import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.entity.OrderOutboxEntity;
import com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.exception.OrderOutboxNotFoundException;
import com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.mapper.OrderOutboxDataAccessMapper;
import com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.repository.OrderOutboxJpaRepository;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.andrei.food.ordering.system.service.valueobject.OrderApprovalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderOutboxRepositoryImplTest {

    @Mock
    private OrderOutboxJpaRepository orderOutboxJpaRepository;
    @Mock
    private OrderOutboxDataAccessMapper orderOutboxDataAccessMapper;

    @InjectMocks
    private OrderOutboxRepositoryImpl orderOutboxRepositoryImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Saves order outbox message successfully")
    @Test
    void savesOrderOutboxMessageSuccessfully() {

        OrderOutboxMessage message = OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .createdAt(ZonedDateTime.now())
                .type("type")
                .payload("payload")
                .outboxStatus(OutboxStatus.STARTED)
                .approvalStatus(OrderApprovalStatus.APPROVED)
                .version(1)
                .build();

        OrderOutboxEntity entity = OrderOutboxEntity.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .createdAt(ZonedDateTime.now())
                .type("type")
                .payload("payload")
                .outboxStatus(OutboxStatus.STARTED)
                .approvalStatus(OrderApprovalStatus.APPROVED)
                .version(1)
                .build();
        when(orderOutboxJpaRepository.save(any())).thenReturn(entity);
        when(orderOutboxDataAccessMapper.orderOutboxEntityToOrderOutboxMessage(any()))
                .thenReturn(message);

        OrderOutboxMessage result = orderOutboxRepositoryImpl.save(message);

        assertEquals(message, result);
        verify(orderOutboxJpaRepository).save(any());
    }


    @DisplayName("Finds order outbox messages by type and status successfully")
    @Test
    void findsOrderOutboxMessagesByTypeAndStatusSuccessfully() {
        OrderOutboxMessage message = OrderOutboxMessage.builder().id(UUID.randomUUID()).build();
        OrderOutboxEntity entity = OrderOutboxEntity.builder().id(UUID.randomUUID()).build();
        List<OrderOutboxMessage> messages = Collections.singletonList(message);
        when(orderOutboxJpaRepository.findByTypeAndOutboxStatus(anyString(), any()))
                .thenReturn(Optional.of(Collections.singletonList(entity)));
        when(orderOutboxDataAccessMapper.orderOutboxEntityToOrderOutboxMessage(any())).thenReturn(message);

        Optional<List<OrderOutboxMessage>> result = orderOutboxRepositoryImpl.findByTypeAndOutboxStatus("sagaType", OutboxStatus.STARTED);

        assertTrue(result.isPresent());
        assertEquals(messages, result.get());
    }

    @DisplayName("Throws exception when order outbox messages not found by type and status")
    @Test
    void throwsExceptionWhenOrderOutboxMessagesNotFoundByTypeAndStatus() {
        when(orderOutboxJpaRepository.findByTypeAndOutboxStatus(anyString(), any())).thenReturn(Optional.empty());

        assertThrows(OrderOutboxNotFoundException.class, () -> orderOutboxRepositoryImpl.findByTypeAndOutboxStatus("sagaType", OutboxStatus.STARTED));
    }

    @DisplayName("Finds order outbox message by type, saga id, and status successfully")
    @Test
    void findsOrderOutboxMessageByTypeSagaIdAndStatusSuccessfully() {
        OrderOutboxMessage message = OrderOutboxMessage.builder().id(UUID.randomUUID()).build();
        OrderOutboxEntity entity = OrderOutboxEntity.builder().id(UUID.randomUUID()).build();
        when(orderOutboxJpaRepository.findByTypeAndSagaIdAndOutboxStatus(anyString(), any(), any()))
                .thenReturn(Optional.of(entity));
        when(orderOutboxDataAccessMapper.orderOutboxEntityToOrderOutboxMessage(any()))
                .thenReturn(message);

        Optional<OrderOutboxMessage> result = orderOutboxRepositoryImpl.findByTypeAndSagaIdAndOutboxStatus("type", UUID.randomUUID(), OutboxStatus.STARTED);

        assertTrue(result.isPresent());
        assertEquals(message, result.get());
    }

    @DisplayName("Deletes order outbox messages by type and status successfully")
    @Test
    void deletesOrderOutboxMessagesByTypeAndStatusSuccessfully() {
        doNothing().when(orderOutboxJpaRepository).deleteByTypeAndOutboxStatus(anyString(), any());

        orderOutboxRepositoryImpl.deleteByTypeAndOutboxStatus("type", OutboxStatus.STARTED);

        verify(orderOutboxJpaRepository).deleteByTypeAndOutboxStatus(anyString(), any());
    }
}