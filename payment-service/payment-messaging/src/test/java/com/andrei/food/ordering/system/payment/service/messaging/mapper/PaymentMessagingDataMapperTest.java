package com.andrei.food.ordering.system.payment.service.messaging.mapper;

import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentOrderStatus;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.andrei.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.andrei.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMessagingDataMapperTest {

    private PaymentMessagingDataMapper paymentMessagingDataMapper = new PaymentMessagingDataMapper();

    @Test
    @DisplayName("Maps PaymentRequestAvroModel to PaymentRequest correctly")
    void mapsPaymentRequestAvroModelToPaymentRequestCorrectly() {
        PaymentRequestAvroModel avroModel = PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setCustomerId(UUID.randomUUID())
                .setOrderId(UUID.randomUUID())
                .setPrice(new BigDecimal(100))
                .setCreatedAt(Instant.now())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();

        PaymentRequest paymentRequest = paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(avroModel);

        assertEquals(avroModel.getId().toString(), paymentRequest.getId());
        assertEquals(avroModel.getSagaId().toString(), paymentRequest.getSagaId());
        assertEquals(avroModel.getCustomerId().toString(), paymentRequest.getCustomerId());
        assertEquals(avroModel.getOrderId().toString(), paymentRequest.getOrderId());
        assertEquals(avroModel.getPrice(), paymentRequest.getPrice());
        assertEquals(avroModel.getCreatedAt(), paymentRequest.getCreatedAt());
        assertEquals(com.andrei.food.ordering.system.service.valueobject.PaymentOrderStatus.valueOf(avroModel.getPaymentOrderStatus().name()), paymentRequest.getPaymentOrderStatus());
    }

    @Test
    @DisplayName("Maps OrderEventPayload to PaymentResponseAvroModel correctly")
    void mapsOrderEventPayloadToPaymentResponseAvroModelCorrectly() {
        String sagaId = UUID.randomUUID().toString();
        OrderEventPayload orderEventPayload = OrderEventPayload.builder()
                .customerId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .price(new BigDecimal(100))
                .createdAt(ZonedDateTime.now())
                .paymentId(UUID.randomUUID().toString())
                .paymentStatus("COMPLETED")
                .failureMessages(List.of("Failure message"))
                .build();

        PaymentResponseAvroModel avroModel = paymentMessagingDataMapper.orderEventPayloadToPaymentResponseAvroModel(sagaId, orderEventPayload);

        assertEquals(UUID.fromString(sagaId), avroModel.getSagaId());
        assertEquals(UUID.fromString(orderEventPayload.getCustomerId()), avroModel.getCustomerId());
        assertEquals(UUID.fromString(orderEventPayload.getOrderId()), avroModel.getOrderId());
        assertEquals(orderEventPayload.getPrice(), avroModel.getPrice());
        assertEquals(orderEventPayload.getCreatedAt().toInstant().truncatedTo(ChronoUnit.MILLIS), avroModel.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(PaymentStatus.valueOf(orderEventPayload.getPaymentStatus()), avroModel.getPaymentStatus());
        assertEquals(orderEventPayload.getFailureMessages(), avroModel.getFailureMessages());
    }
}