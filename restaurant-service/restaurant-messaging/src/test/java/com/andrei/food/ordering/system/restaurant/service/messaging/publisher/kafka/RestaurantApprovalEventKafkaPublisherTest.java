package com.andrei.food.ordering.system.restaurant.service.messaging.publisher.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.kafka.order.avro.model.Product;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.andrei.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.restaurant.service.domain.config.RestaurantServiceConfigData;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import com.andrei.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

class RestaurantApprovalEventKafkaPublisherTest {

    @Mock
    private KafkaProducer kafkaProducer;
    @Mock
    private KafkaMessageHelper kafkaMessageHelper;
    @Mock
    private RestaurantMessagingDataMapper restaurantMessagingDataMapper;
    @Mock
    private RestaurantServiceConfigData restaurantServiceConfigData;

    @InjectMocks
    private RestaurantApprovalEventKafkaPublisher restaurantApprovalEventKafkaPublisher;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logCaptor = LogCaptor.forClass(RestaurantApprovalEventKafkaPublisher.class);
    }

    @DisplayName("Publishes order approval event successfully")
    @Test
    void publishesOrderApprovalEventSuccessfully() {
        UUID sagaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        OrderOutboxMessage orderOutboxMessage = mock(OrderOutboxMessage.class);
        OrderEventPayload orderEventPayload = mock(OrderEventPayload.class);
        when(orderOutboxMessage.getPayload()).thenReturn("payload");
        when(kafkaMessageHelper.getOrderEventPayload("payload", OrderEventPayload.class)).thenReturn(orderEventPayload);
        when(orderOutboxMessage.getSagaId()).thenReturn(sagaId);
        when(orderEventPayload.getOrderId()).thenReturn(orderId.toString());

        RestaurantApprovalResponseAvroModel avroModel = RestaurantApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(sagaId)
                .setOrderId(orderId)
                .setRestaurantId(UUID.randomUUID())
                .setCreatedAt(ZonedDateTime.now().toInstant())
                .setOrderApprovalStatus(com.andrei.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus.APPROVED)
                .setFailureMessages(new ArrayList<>())
                .build();

        when(restaurantMessagingDataMapper.orderEventPayloadToRestaurantApprovalResponseAvroModel(anyString(), eq(orderEventPayload)))
                .thenReturn(avroModel);

        String topicName = "restaurant-approval-response-topic"; // Mocked topic name
        when(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName()).thenReturn(topicName);

        restaurantApprovalEventKafkaPublisher.publish(orderOutboxMessage, (msg, status) -> {});

        verify(kafkaProducer).send(eq(topicName), anyString(), eq(avroModel), any());
        assertEquals(2, logCaptor.getInfoLogs().size());
        assertEquals("Received OrderOutboxMessage for order id: "+orderId+" and saga id: "+sagaId, logCaptor.getInfoLogs().get(0));
        assertEquals("RestaurantApprovalResponseAvroModel sent to kafka for order id: "+orderId+" and saga id: "+sagaId, logCaptor.getInfoLogs().get(1));
    }

    @DisplayName("Handles exception when publishing order approval event")
    @Test
    void handlesExceptionWhenPublishingOrderApprovalEvent() {
        UUID sagaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        OrderOutboxMessage orderOutboxMessage = mock(OrderOutboxMessage.class);
        OrderEventPayload orderEventPayload = mock(OrderEventPayload.class);
        when(orderOutboxMessage.getPayload()).thenReturn("payload");
        when(kafkaMessageHelper.getOrderEventPayload("payload", OrderEventPayload.class)).thenReturn(orderEventPayload);
        when(orderOutboxMessage.getSagaId()).thenReturn(sagaId);
        when(orderEventPayload.getOrderId()).thenReturn(orderId.toString());

        when(restaurantMessagingDataMapper.orderEventPayloadToRestaurantApprovalResponseAvroModel(anyString(), eq(orderEventPayload)))
                .thenThrow(new RuntimeException("Test exception"));

        String topicName = "restaurant-approval-response-topic"; // Mocked topic name
        when(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName()).thenReturn(topicName);

        restaurantApprovalEventKafkaPublisher.publish(orderOutboxMessage, (msg, status) -> {});

        assertEquals(1, logCaptor.getErrorLogs().size());
        assertEquals("Error while sending RestaurantApprovalResponseAvroModel message to kafka with order id: "+orderId+" and saga id: "+sagaId+", error: Test exception", logCaptor.getErrorLogs().get(0));
    }
}