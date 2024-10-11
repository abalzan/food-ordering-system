package com.andrei.food.ordering.system.restaurant.service.messaging.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.andrei.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.andrei.food.ordering.system.kafka.order.avro.model.Product;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.andrei.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import com.andrei.food.ordering.system.service.valueobject.RestaurantOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.UUID;

class RestaurantMessagingDataMapperTest {

    @InjectMocks
    private RestaurantMessagingDataMapper restaurantMessagingDataMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Returns RestaurantApprovalRequest from AvroModel")
    void restaurantApprovalRequestAvroModelToRestaurantApprovalReturnsRequest() {
        RestaurantApprovalRequestAvroModel avroModel = RestaurantApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setRestaurantId(UUID.randomUUID())
                .setOrderId(UUID.randomUUID())
                .setRestaurantOrderStatus(com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantOrderStatus.PAID)
                .setProducts(Collections.singletonList(Product.newBuilder()
                        .setId(UUID.randomUUID().toString())
                        .setQuantity(10)
                        .build()))
                .setPrice(new BigDecimal("100"))
                .setCreatedAt(ZonedDateTime.now().toInstant())
                .build();

        RestaurantApprovalRequest result = restaurantMessagingDataMapper.restaurantApprovalRequestAvroModelToRestaurantApproval(avroModel);

        assertNotNull(result);
        assertEquals(avroModel.getId().toString(), result.getId());
        assertEquals(avroModel.getSagaId().toString(), result.getSagaId());
        assertEquals(avroModel.getRestaurantId().toString(), result.getRestaurantId());
        assertEquals(avroModel.getOrderId().toString(), result.getOrderId());
        assertEquals(RestaurantOrderStatus.PAID, result.getRestaurantOrderStatus());
        assertEquals(avroModel.getProducts().get(0).getId(), result.getProducts().get(0).getId().getValue().toString());
        assertEquals(avroModel.getProducts().get(0).getQuantity(), result.getProducts().get(0).getQuantity());
        assertEquals(avroModel.getPrice(), result.getPrice());
        assertEquals(avroModel.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    @DisplayName("Returns RestaurantApprovalResponseAvroModel from OrderEventPayload")
    void orderEventPayloadToRestaurantApprovalResponseAvroModelReturnsAvroModel() {
        String sagaId = UUID.randomUUID().toString();
        OrderEventPayload orderEventPayload = OrderEventPayload.builder()
                .orderId(UUID.randomUUID().toString())
                .restaurantId(UUID.randomUUID().toString())
                .createdAt(ZonedDateTime.now())
                .orderApprovalStatus("APPROVED")
                .failureMessages(Collections.emptyList())
                .build();

        RestaurantApprovalResponseAvroModel result = restaurantMessagingDataMapper.orderEventPayloadToRestaurantApprovalResponseAvroModel(sagaId, orderEventPayload);

        assertNotNull(result);
        assertEquals(sagaId, result.getSagaId().toString());
        assertEquals(orderEventPayload.getOrderId(), result.getOrderId().toString());
        assertEquals(orderEventPayload.getRestaurantId(), result.getRestaurantId().toString());
        assertEquals(orderEventPayload.getCreatedAt().toInstant().truncatedTo(ChronoUnit.MILLIS), result.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));        assertEquals(OrderApprovalStatus.APPROVED, result.getOrderApprovalStatus());
        assertTrue(result.getFailureMessages().isEmpty());
    }

    @Test
    @DisplayName("Handles null OrderEventPayload gracefully")
    void orderEventPayloadToRestaurantApprovalResponseAvroModelHandlesNullPayload() {
        String sagaId = UUID.randomUUID().toString();

        assertThrows(NullPointerException.class, () -> {
            restaurantMessagingDataMapper.orderEventPayloadToRestaurantApprovalResponseAvroModel(sagaId, null);
        });
    }
}