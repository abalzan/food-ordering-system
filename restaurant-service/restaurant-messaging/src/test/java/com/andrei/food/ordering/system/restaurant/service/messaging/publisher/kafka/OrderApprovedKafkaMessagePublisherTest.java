package com.andrei.food.ordering.system.restaurant.service.messaging.publisher.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.andrei.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.restaurant.service.domain.config.RestaurantServiceConfigData;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.andrei.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.service.valueobject.RestaurantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

class OrderApprovedKafkaMessagePublisherTest {

    @Mock
    private RestaurantMessagingDataMapper restaurantMessagingDataMapper;

    @Mock
    private KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;

    @Mock
    private RestaurantServiceConfigData restaurantServiceConfigData;

    @Mock
    private KafkaMessageHelper kafkaMessageHelper;

    @InjectMocks
    private OrderApprovedKafkaMessagePublisher orderApprovedKafkaMessagePublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void publishSendsMessageSuccessfully() {
        OrderId orderUUID = new OrderId(UUID.randomUUID());
        OrderApproval orderApproval = OrderApproval.builder().orderId(orderUUID).build();
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(
                orderApproval,
                new RestaurantId(UUID.randomUUID()),
                Collections.emptyList(),
                ZonedDateTime.now(),
                orderApprovedKafkaMessagePublisher
        );
        RestaurantApprovalResponseAvroModel avroModel = mock(RestaurantApprovalResponseAvroModel.class);
        String topicName = "test-topic";
        String orderId = orderUUID.getValue().toString();

        when(restaurantMessagingDataMapper.orderApprovedEventToRestaurantApprovalResponseAvroModel(orderApprovedEvent)).thenReturn(avroModel);
        when(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName()).thenReturn(topicName);
        when(kafkaMessageHelper.getKafkaCallback(anyString(), any(), anyString(), anyString())).thenReturn(null);

        orderApprovedKafkaMessagePublisher.publish(orderApprovedEvent);

        verify(kafkaProducer, times(1)).send(eq(topicName), eq(orderId), eq(avroModel), any());
    }

    @Test
    void publishHandlesExceptionGracefully() {
        OrderId orderUUID = new OrderId(UUID.randomUUID());
        OrderApproval orderApproval = OrderApproval.builder().orderId(orderUUID).build();
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(
                orderApproval,
                new RestaurantId(UUID.randomUUID()),
                Collections.emptyList(),
                ZonedDateTime.now(),
                orderApprovedKafkaMessagePublisher
        );

        when(restaurantMessagingDataMapper.orderApprovedEventToRestaurantApprovalResponseAvroModel(orderApprovedEvent)).thenThrow(new RuntimeException("Test exception"));

        assertDoesNotThrow(() -> orderApprovedKafkaMessagePublisher.publish(orderApprovedEvent));
    }
}