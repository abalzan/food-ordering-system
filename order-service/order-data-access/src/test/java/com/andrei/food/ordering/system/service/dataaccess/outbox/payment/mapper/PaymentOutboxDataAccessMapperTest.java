package com.andrei.food.ordering.system.service.dataaccess.outbox.payment.mapper;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.dataaccess.outbox.payment.entity.PaymentOutboxEntity;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.andrei.food.ordering.system.service.valueobject.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentOutboxDataAccessMapperTest {

    private PaymentOutboxDataAccessMapper paymentOutboxDataAccessMapper;

    @BeforeEach
    void setUp() {
        paymentOutboxDataAccessMapper = new PaymentOutboxDataAccessMapper();
    }

    @Test
    void orderPaymentOutboxMessageToOutboxEntityMapsCorrectly() {
        OrderPaymentOutboxMessage message = OrderPaymentOutboxMessage.builder()
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

        PaymentOutboxEntity entity = paymentOutboxDataAccessMapper.orderPaymentOutboxMessageToOutboxEntity(message);

        assertEquals(message.getCreatedAt(), entity.getCreatedAt());
        assertEquals(message.getType(), entity.getType());
        assertEquals(message.getPayload(), entity.getPayload());
        assertEquals(message.getOrderStatus(), entity.getOrderStatus());
        assertEquals(message.getSagaStatus(), entity.getSagaStatus());
        assertEquals(message.getId(), entity.getId());
        assertEquals(message.getSagaId(), entity.getSagaId());
        assertEquals(message.getOutboxStatus(), entity.getOutboxStatus());
        assertEquals(message.getVersion(), entity.getVersion());
    }

    @Test
    void paymentOutboxEntityToOrderPaymentOutboxMessageMapsCorrectly() {
        PaymentOutboxEntity entity = PaymentOutboxEntity.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .createdAt(ZonedDateTime.now())
                .type("type")
                .payload("payload")
                .orderStatus(OrderStatus.CANCELLING)
                .sagaStatus(SagaStatus.PROCESSING)
                .outboxStatus(OutboxStatus.COMPLETED)
                .version(1)
                .build();

        OrderPaymentOutboxMessage message = paymentOutboxDataAccessMapper.paymentOutboxEntityToOrderPaymentOutboxMessage(entity);

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