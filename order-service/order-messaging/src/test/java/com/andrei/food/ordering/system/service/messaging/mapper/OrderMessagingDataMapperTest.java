package com.andrei.food.ordering.system.service.messaging.mapper;

import com.andrei.food.ordering.service.domain.dto.message.PaymentResponse;
import com.andrei.food.ordering.service.domain.dto.message.RestaurantApprovalResponse;
import com.andrei.food.ordering.system.domain.entity.Order;
import com.andrei.food.ordering.system.domain.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.domain.event.OrderCreatedEvent;
import com.andrei.food.ordering.system.domain.event.OrderPaidEvent;
import com.andrei.food.ordering.system.domain.valueobject.*;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class OrderMessagingDataMapperTest {

    @Mock
    private OrderCreatedEvent orderCreatedEvent;
    @Mock
    private OrderCancelledEvent orderCancelledEvent;
    @Mock
    private OrderPaidEvent orderPaidEvent;
    @Mock
    private Order order;
    @Mock
    private PaymentResponseAvroModel paymentResponseAvroModel;

    private OrderMessagingDataMapper orderMessagingDataMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(order.getId()).thenReturn(new OrderId(UUID.randomUUID()));
        when(order.getPrice()).thenReturn(new Money(new BigDecimal("100.0")));
        when(order.getCustomerId()).thenReturn(new CustomerId(UUID.randomUUID()));
        when(orderCancelledEvent.getCreatedAt()).thenReturn(ZonedDateTime.now());
        when(orderCreatedEvent.getCreatedAt()).thenReturn(ZonedDateTime.now());
        orderMessagingDataMapper = new OrderMessagingDataMapper();
    }

    @Test
    @DisplayName("Should map OrderCreatedEvent to PaymentRequestAvroModel correctly")
    void shouldMapOrderCreatedEventToPaymentRequestAvroModelCorrectly() {
        when(orderCreatedEvent.getOrder()).thenReturn(order);
        PaymentRequestAvroModel result = orderMessagingDataMapper.orderCreatedEventToPaymentRequestAvroModel(orderCreatedEvent);
        assertEquals(order.getCustomerId().getValue(), result.getCustomerId());
    }

    @Test
    @DisplayName("Should map OrderCancelledEvent to PaymentRequestAvroModel correctly")
    void shouldMapOrderCancelledEventToPaymentRequestAvroModelCorrectly() {
        when(orderCancelledEvent.getOrder()).thenReturn(order);
        PaymentRequestAvroModel result = orderMessagingDataMapper.orderCancelledEventToPaymentRequestAvroModel(orderCancelledEvent);
        assertEquals(order.getCustomerId().getValue(), result.getCustomerId());
    }

    @Test
    @DisplayName("Should map OrderPaidEvent to RestaurantApprovalRequestAvroModel correctly")
    void shouldMapOrderPaidEventToRestaurantApprovalRequestAvroModelCorrectly() {
        when(orderPaidEvent.getOrder()).thenReturn(order);
        when(order.getId()).thenReturn(new OrderId(UUID.randomUUID()));
        when(order.getRestaurantId()).thenReturn(new RestaurantId(UUID.randomUUID()));
        when(order.getOrderStatus()).thenReturn(OrderStatus.PAID);
        when(orderPaidEvent.getCreatedAt()).thenReturn(ZonedDateTime.now());

        RestaurantApprovalRequestAvroModel result = orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(orderPaidEvent);

        assertEquals(order.getId().getValue(), result.getOrderId());
        assertEquals(order.getRestaurantId().getValue(), result.getRestaurantId());
        assertEquals(order.getOrderStatus().name(), result.getRestaurantOrderStatus().name());
    }

    @Test
    @DisplayName("Should correctly map PaymentResponseAvroModel to PaymentResponse")
    void shouldCorrectlyMapPaymentResponseAvroModelToPaymentResponse() {
        UUID id = UUID.randomUUID();
        UUID sagaId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        BigDecimal price = new BigDecimal("100.0");
        Instant createdAt = Instant.now();
        PaymentStatus paymentStatus = PaymentStatus.CANCELLED;

        when(paymentResponseAvroModel.getId()).thenReturn(id);
        when(paymentResponseAvroModel.getSagaId()).thenReturn(sagaId);
        when(paymentResponseAvroModel.getPaymentId()).thenReturn(paymentId);
        when(paymentResponseAvroModel.getCustomerId()).thenReturn(customerId);
        when(paymentResponseAvroModel.getOrderId()).thenReturn(orderId);
        when(paymentResponseAvroModel.getPrice()).thenReturn(price);
        when(paymentResponseAvroModel.getCreatedAt()).thenReturn(createdAt);
        when(paymentResponseAvroModel.getPaymentStatus()).thenReturn(com.andrei.food.ordering.system.kafka.order.avro.model.PaymentStatus.valueOf(paymentStatus.name()));
        when(paymentResponseAvroModel.getFailureMessages()).thenReturn(Collections.emptyList());

        PaymentResponse result = orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel);

        assertEquals(id.toString(), result.getId());
        assertEquals(sagaId.toString(), result.getSagaId());
        assertEquals(paymentId.toString(), result.getPaymentId());
        assertEquals(customerId.toString(), result.getCustomerId());
        assertEquals(orderId.toString(), result.getOrderId());
        assertEquals(price, result.getPrice());
        assertEquals(createdAt, result.getCreateAt());
        assertEquals(paymentStatus, result.getPaymentStatus());
        assertEquals(Collections.emptyList(), result.getFailureMessages());
    }

    @Test
    @DisplayName("Successfully maps Avro model to domain response for approved status")
    void successfullyMapsAvroModelToDomainResponseForApprovedStatus() {
        RestaurantApprovalResponseAvroModel avroModel = RestaurantApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setOrderId(UUID.randomUUID())
                .setRestaurantId(UUID.randomUUID())
                .setCreatedAt(Instant.now())
                .setOrderApprovalStatus(com.andrei.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus.APPROVED)
                .setFailureMessages(Collections.emptyList())
                .build();

        RestaurantApprovalResponse response = orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(avroModel);

        assertEquals(OrderApprovalStatus.APPROVED, response.getOrderApprovalStatus());
        assertEquals(Collections.emptyList(), response.getFailureMessages());
    }

    @Test
    @DisplayName("Successfully maps Avro model to domain response with failure messages")
    void successfullyMapsAvroModelToDomainResponseWithFailureMessages() {
        RestaurantApprovalResponseAvroModel avroModel = RestaurantApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setOrderId(UUID.randomUUID())
                .setRestaurantId(UUID.randomUUID())
                .setCreatedAt(Instant.now())
                .setOrderApprovalStatus(com.andrei.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus.REJECTED)
                .setFailureMessages(Collections.singletonList("Insufficient inventory"))
                .build();

        RestaurantApprovalResponse response = orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(avroModel);

        assertEquals(OrderApprovalStatus.REJECTED, response.getOrderApprovalStatus());
        assertEquals(Collections.singletonList("Insufficient inventory"), response.getFailureMessages());
    }

}