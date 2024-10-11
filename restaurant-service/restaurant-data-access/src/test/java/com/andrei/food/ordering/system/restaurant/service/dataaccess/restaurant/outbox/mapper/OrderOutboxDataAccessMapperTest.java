package com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.mapper;

import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.entity.OrderOutboxEntity;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.andrei.food.ordering.system.service.valueobject.OrderApprovalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
class OrderOutboxDataAccessMapperTest {

    @InjectMocks
    private OrderOutboxDataAccessMapper orderOutboxDataAccessMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Converts OrderOutboxMessage to OrderOutboxEntity successfully")
    @Test
    void convertsOrderOutboxMessageToOrderOutboxEntitySuccessfully() {
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

        OrderOutboxEntity entity = orderOutboxDataAccessMapper.orderOutboxMessageToOutboxEntity(message);

        assertEquals(message.getId(), entity.getId());
        assertEquals(message.getSagaId(), entity.getSagaId());
        assertEquals(message.getCreatedAt(), entity.getCreatedAt());
        assertEquals(message.getType(), entity.getType());
        assertEquals(message.getPayload(), entity.getPayload());
        assertEquals(message.getOutboxStatus(), entity.getOutboxStatus());
        assertEquals(message.getApprovalStatus(), entity.getApprovalStatus());
        assertEquals(message.getVersion(), entity.getVersion());
    }

    @DisplayName("Converts OrderOutboxEntity to OrderOutboxMessage successfully")
    @Test
    void convertsOrderOutboxEntityToOrderOutboxMessageSuccessfully() {
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

        OrderOutboxMessage message = orderOutboxDataAccessMapper.orderOutboxEntityToOrderOutboxMessage(entity);

        assertEquals(entity.getId(), message.getId());
        assertEquals(entity.getSagaId(), message.getSagaId());
        assertEquals(entity.getCreatedAt(), message.getCreatedAt());
        assertEquals(entity.getType(), message.getType());
        assertEquals(entity.getPayload(), message.getPayload());
        assertEquals(entity.getOutboxStatus(), message.getOutboxStatus());
        assertEquals(entity.getApprovalStatus(), message.getApprovalStatus());
        assertEquals(entity.getVersion(), message.getVersion());
    }
}