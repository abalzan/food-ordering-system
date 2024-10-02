package com.andrei.food.ordering.system.service.messaging.mapper;

import com.andrei.food.ordering.system.kafka.order.avro.model.*;
import com.andrei.food.ordering.system.service.domain.dto.message.PaymentResponse;
import com.andrei.food.ordering.system.service.domain.dto.message.RestaurantApprovalResponse;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderMessagingDataMapperTest {

    private OrderMessagingDataMapper orderMessagingDataMapper;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    @BeforeEach
    void setUp() {
        orderMessagingDataMapper = new OrderMessagingDataMapper();
    }

    @Test
    @DisplayName("Converts PaymentResponseAvroModel to PaymentResponse correctly")
    void convertsPaymentResponseAvroModelToPaymentResponseCorrectly() {
        PaymentResponseAvroModel avroModel = new PaymentResponseAvroModel(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("100.00"), createdAt.toInstant(), PaymentStatus.COMPLETED, Collections.emptyList()
        );

        PaymentResponse response = orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(avroModel);

        assertEquals(avroModel.getId().toString(), response.getId());
        assertEquals(avroModel.getSagaId().toString(), response.getSagaId());
        assertEquals(avroModel.getPaymentId().toString(), response.getPaymentId());
        assertEquals(avroModel.getCustomerId().toString(), response.getCustomerId());
        assertEquals(avroModel.getOrderId().toString(), response.getOrderId());
        assertEquals(avroModel.getPrice(), response.getPrice());
        assertEquals(avroModel.getCreatedAt(), response.getCreateAt());
        assertEquals(avroModel.getPaymentStatus().name(), response.getPaymentStatus().name());
        assertEquals(avroModel.getFailureMessages(), response.getFailureMessages());
    }

    @Test
    @DisplayName("Converts RestaurantApprovalResponseAvroModel to RestaurantApprovalResponse correctly")
    void convertsRestaurantApprovalResponseAvroModelToRestaurantApprovalResponseCorrectly() {
        RestaurantApprovalResponseAvroModel avroModel = new RestaurantApprovalResponseAvroModel(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), createdAt.toInstant(),
                OrderApprovalStatus.APPROVED, Collections.emptyList()
        );

        RestaurantApprovalResponse response = orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(avroModel);

        assertEquals(avroModel.getId().toString(), response.getId());
        assertEquals(avroModel.getSagaId().toString(), response.getSagaId());
        assertEquals(avroModel.getOrderId().toString(), response.getOrderId());
        assertEquals(avroModel.getRestaurantId().toString(), response.getRestaurantId());
        assertEquals(avroModel.getCreatedAt(), response.getCreateAt());
        assertEquals(avroModel.getOrderApprovalStatus().name(), response.getOrderApprovalStatus().name());
        assertEquals(avroModel.getFailureMessages(), response.getFailureMessages());
    }

    @Test
    @DisplayName("Converts OrderPaymentEventPayload to PaymentRequestAvroModel correctly")
    void convertsOrderPaymentEventPayloadToPaymentRequestAvroModelCorrectly() {
        OrderPaymentEventPayload payload = new OrderPaymentEventPayload(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), new BigDecimal("100.00"),
                createdAt, "PENDING"
        );

        PaymentRequestAvroModel avroModel = orderMessagingDataMapper.orderPaymentEventToPaymentRequestAvroModel(UUID.randomUUID().toString(), payload);

        assertEquals(payload.getCustomerId(), avroModel.getCustomerId().toString());
        assertEquals(payload.getOrderId(), avroModel.getOrderId().toString());
        assertEquals(payload.getPrice(), avroModel.getPrice());
        assertEquals(payload.getCreatedAt().toInstant().truncatedTo(ChronoUnit.MILLIS), avroModel.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(payload.getPaymentOrderStatus(), avroModel.getPaymentOrderStatus().name());
    }

    @Test
    @DisplayName("Converts OrderApprovalEventPayload to RestaurantApprovalRequestAvroModel correctly")
    void convertsOrderApprovalEventPayloadToRestaurantApprovalRequestAvroModelCorrectly() {
        OrderApprovalEventPayload payload = new OrderApprovalEventPayload(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), new BigDecimal("100.00"),
                createdAt, "PAID",
                Collections.emptyList()
        );

        RestaurantApprovalRequestAvroModel avroModel = orderMessagingDataMapper.orderApprovalEventToRestaurantApprovalRequestAvroModel(UUID.randomUUID().toString(), payload);

        assertEquals(payload.getOrderId(), avroModel.getOrderId().toString());
        assertEquals(payload.getRestaurantId(), avroModel.getRestaurantId().toString());
        assertEquals(payload.getRestaurantOrderStatus(), avroModel.getRestaurantOrderStatus().name());
        assertEquals(payload.getPrice(), avroModel.getPrice());
        assertEquals(payload.getCreatedAt().toInstant().truncatedTo(ChronoUnit.MILLIS), avroModel.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(payload.getProducts().size(), avroModel.getProducts().size());
    }
}