package com.andrei.food.ordering.system.restaurant.service.messaging.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.andrei.food.ordering.system.kafka.order.avro.model.Product;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.andrei.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import com.andrei.food.ordering.system.restaurant.service.messaging.publisher.kafka.OrderApprovedKafkaMessagePublisher;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.service.valueobject.RestaurantId;
import com.andrei.food.ordering.system.service.valueobject.RestaurantOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

class RestaurantMessagingDataMapperTest {

    @Mock
    private DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher;
    @Mock
    private DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher;

    @InjectMocks
    private RestaurantMessagingDataMapper restaurantMessagingDataMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void orderApprovedEventToRestaurantApprovalResponseAvroModelReturnsAvroModel() {
        OrderId orderUUID = new OrderId(UUID.randomUUID());
        RestaurantId restaurantUUID = new RestaurantId(UUID.randomUUID());
        OrderApproval orderApproval = OrderApproval.builder()
                .orderId(orderUUID)
                .approvalStatus(com.andrei.food.ordering.system.service.valueobject.OrderApprovalStatus.APPROVED)
                .build();
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(orderApproval, restaurantUUID,  List.of(), ZonedDateTime.now(), orderApprovedEventDomainEventPublisher);

        RestaurantApprovalResponseAvroModel result = restaurantMessagingDataMapper.orderApprovedEventToRestaurantApprovalResponseAvroModel(orderApprovedEvent);

        assertNotNull(result);
        assertEquals(OrderApprovalStatus.APPROVED, result.getOrderApprovalStatus());
    }

    @Test
    void orderRejectedEventToRestaurantApprovalResponseAvroModelReturnsAvroModel() {
        OrderId orderUUID = new OrderId(UUID.randomUUID());
        RestaurantId restaurantUUID = new RestaurantId(UUID.randomUUID());
        OrderApproval orderApproval = OrderApproval.builder()
                .orderId(orderUUID)
                .approvalStatus(com.andrei.food.ordering.system.service.valueobject.OrderApprovalStatus.REJECTED)
                .build();
        OrderRejectedEvent orderRejectedEvent = new OrderRejectedEvent(orderApproval, restaurantUUID,  List.of("Reason"), ZonedDateTime.now(), orderRejectedEventDomainEventPublisher);

        RestaurantApprovalResponseAvroModel result = restaurantMessagingDataMapper.orderRejectedEventToRestaurantApprovalResponseAvroModel(orderRejectedEvent);

        assertNotNull(result);
        assertEquals(OrderApprovalStatus.REJECTED, result.getOrderApprovalStatus());
        assertEquals("Reason", result.getFailureMessages().get(0));
    }

    @Test
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
        assertEquals(RestaurantOrderStatus.PAID, result.getRestaurantOrderStatus());
    }
}