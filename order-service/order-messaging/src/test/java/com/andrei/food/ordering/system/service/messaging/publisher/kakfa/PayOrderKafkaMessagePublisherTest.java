package com.andrei.food.ordering.system.service.messaging.publisher.kakfa;

import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.event.OrderPaidEvent;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import com.andrei.food.ordering.system.service.domain.config.OrderServiceConfigData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PayOrderKafkaMessagePublisherTest {

    @Mock
    private OrderMessagingDataMapper orderMessagingDataMapper;
    @Mock
    private OrderServiceConfigData orderServiceConfigData;
    @Mock
    private KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    @Mock
    private OrderKafkaMessageHelper orderKafkaMessageHelper;
    @Mock
    private OrderPaidEvent orderPaidEvent;
    @Mock
    private Order order;
    @Mock
    private OrderId orderId;

    private PayOrderKafkaMessagePublisher payOrderKafkaMessagePublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(orderPaidEvent.getOrder()).thenReturn(order);
        when(order.getId()).thenReturn(orderId);
        when(orderId.getValue()).thenReturn(java.util.UUID.randomUUID());
        payOrderKafkaMessagePublisher = new PayOrderKafkaMessagePublisher(orderMessagingDataMapper, orderServiceConfigData, kafkaProducer, orderKafkaMessageHelper);
    }

    @Test
    @DisplayName("Should publish order paid event successfully")
    void shouldPublishOrderPaidEventSuccessfully() {
        when(orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(any(OrderPaidEvent.class))).thenReturn(new RestaurantApprovalRequestAvroModel());
        when(orderServiceConfigData.getRestaurantApprovalRequestTopicName()).thenReturn("testTopic");
        doNothing().when(kafkaProducer).send(anyString(), anyString(), any(RestaurantApprovalRequestAvroModel.class), any());

        payOrderKafkaMessagePublisher.publish(orderPaidEvent);

        verify(kafkaProducer, times(1)).send(anyString(), anyString(), any(RestaurantApprovalRequestAvroModel.class), any());
    }

    @Test
    @DisplayName("Should handle exception when publishing order paid event")
    void shouldHandleExceptionWhenPublishingOrderPaidEvent() {
        when(orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(any(OrderPaidEvent.class))).thenReturn(new RestaurantApprovalRequestAvroModel());
        when(orderServiceConfigData.getRestaurantApprovalRequestTopicName()).thenReturn("testTopic");
        doThrow(new RuntimeException()).when(kafkaProducer).send(anyString(), anyString(), any(RestaurantApprovalRequestAvroModel.class), any());

        payOrderKafkaMessagePublisher.publish(orderPaidEvent);

        verify(kafkaProducer, times(1)).send(anyString(), anyString(), any(RestaurantApprovalRequestAvroModel.class), any());
    }
}