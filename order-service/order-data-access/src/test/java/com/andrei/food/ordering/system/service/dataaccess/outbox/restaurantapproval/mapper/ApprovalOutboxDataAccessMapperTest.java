package com.andrei.food.ordering.system.service.dataaccess.outbox.restaurantapproval.mapper;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.dataaccess.outbox.restaurantapproval.entity.ApprovalOutboxEntity;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.andrei.food.ordering.system.service.valueobject.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ApprovalOutboxDataAccessMapperTest {

    private ApprovalOutboxDataAccessMapper approvalOutboxDataAccessMapper;

    @BeforeEach
    void setUp() {
        approvalOutboxDataAccessMapper = new ApprovalOutboxDataAccessMapper();
    }

    @Test
    void orderCreatedOutboxMessageToOutboxEntityMapsCorrectly() {
        OrderApprovalOutboxMessage message = OrderApprovalOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .createdAt(ZonedDateTime.now())
                .type("type")
                .payload("payload")
                .orderStatus(OrderStatus.APPROVED)
                .sagaStatus(SagaStatus.PROCESSING)
                .outboxStatus(OutboxStatus.COMPLETED)
                .version(1)
                .build();

        ApprovalOutboxEntity entity = approvalOutboxDataAccessMapper.orderCreatedOutboxMessageToOutboxEntity(message);

        assertEquals(message.getId(), entity.getId());
        assertEquals(message.getSagaId(), entity.getSagaId());
        assertEquals(message.getCreatedAt(), entity.getCreatedAt());
        assertEquals(message.getType(), entity.getType());
        assertEquals(message.getPayload(), entity.getPayload());
        assertEquals(message.getOrderStatus(), entity.getOrderStatus());
        assertEquals(message.getSagaStatus(), entity.getSagaStatus());
        assertEquals(message.getOutboxStatus(), entity.getOutboxStatus());
        assertEquals(message.getVersion(), entity.getVersion());
    }

    @Test
    void approvalOutboxEntityToOrderApprovalOutboxMessageMapsCorrectly() {
        ApprovalOutboxEntity entity = ApprovalOutboxEntity.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .createdAt(ZonedDateTime.now())
                .type("type")
                .payload("payload")
                .orderStatus(OrderStatus.CANCELLED)
                .sagaStatus(SagaStatus.SUCCEEDED)
                .outboxStatus(OutboxStatus.FAILED)
                .version(1)
                .build();

        OrderApprovalOutboxMessage message = approvalOutboxDataAccessMapper.approvalOutboxEntityToOrderApprovalOutboxMessage(entity);

        assertEquals(entity.getId(), message.getId());
        assertEquals(entity.getSagaId(), message.getSagaId());
        assertEquals(entity.getCreatedAt(), message.getCreatedAt());
        assertEquals(entity.getType(), message.getType());
        assertEquals(entity.getPayload(), message.getPayload());
        assertEquals(entity.getOrderStatus(), message.getOrderStatus());
        assertEquals(entity.getSagaStatus(), message.getSagaStatus());
        assertEquals(entity.getOutboxStatus(), message.getOutboxStatus());
        assertEquals(entity.getVersion(), message.getVersion());
    }
}