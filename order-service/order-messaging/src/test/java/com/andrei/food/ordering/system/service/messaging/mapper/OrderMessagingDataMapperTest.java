package com.andrei.food.ordering.system.service.messaging.mapper;

import com.andrei.food.ordering.system.domain.entity.Order;
import com.andrei.food.ordering.system.domain.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.domain.event.OrderCreatedEvent;
import com.andrei.food.ordering.system.domain.event.OrderPaidEvent;
import com.andrei.food.ordering.system.domain.valueobject.*;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
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

}