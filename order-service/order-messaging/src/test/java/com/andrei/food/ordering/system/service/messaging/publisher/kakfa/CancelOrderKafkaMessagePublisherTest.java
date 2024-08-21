package com.andrei.food.ordering.system.service.messaging.publisher.kakfa;

import com.andrei.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.andrei.food.ordering.system.service.domain.config.OrderServiceConfigData;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CancelOrderKafkaMessagePublisherTest {

    @Mock
    private OrderMessagingDataMapper orderMessagingDataMapper;
    @Mock
    private OrderServiceConfigData orderServiceConfigData;
    @Mock
    private KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    @Mock
    private KafkaMessageHelper orderKafkaMessageHelper;
    @Mock
    private OrderCancelledEvent orderCancelledEvent;
    @Mock
    private Order order;
    @Mock
    private OrderId orderId;

    private CancelOrderKafkaMessagePublisher cancelOrderKafkaMessagePublisher;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(orderCancelledEvent.getOrder()).thenReturn(order);
        when(order.getId()).thenReturn(orderId);
        when(orderId.getValue()).thenReturn(UUID.randomUUID());
        cancelOrderKafkaMessagePublisher = new CancelOrderKafkaMessagePublisher(orderMessagingDataMapper, orderServiceConfigData, kafkaProducer, orderKafkaMessageHelper);
    }

    @Test
    @DisplayName("Should publish order cancelled event successfully")
    void shouldPublishOrderCancelledEventSuccessfully() {
        // Arrange
        when(orderMessagingDataMapper.orderCancelledEventToPaymentRequestAvroModel(any(OrderCancelledEvent.class))).thenReturn(new PaymentRequestAvroModel());
        when(orderServiceConfigData.getPaymentRequestTopicName()).thenReturn("testTopic");
        doNothing().when(kafkaProducer).send(anyString(), anyString(), any(PaymentRequestAvroModel.class), any());

        // Act
        cancelOrderKafkaMessagePublisher.publish(orderCancelledEvent);

        // Assert
        verify(kafkaProducer, times(1)).send(anyString(), anyString(), any(PaymentRequestAvroModel.class), any());
    }

    @Test
    @DisplayName("Should handle exception when publishing order cancelled event")
    void shouldHandleExceptionWhenPublishingOrderCancelledEvent() {
        // Arrange
        when(orderMessagingDataMapper.orderCancelledEventToPaymentRequestAvroModel(any(OrderCancelledEvent.class))).thenReturn(new PaymentRequestAvroModel());
        when(orderServiceConfigData.getPaymentRequestTopicName()).thenReturn("testTopic");
        doThrow(new RuntimeException()).when(kafkaProducer).send(anyString(), anyString(), any(PaymentRequestAvroModel.class), any());

        // Act
        cancelOrderKafkaMessagePublisher.publish(orderCancelledEvent);

        // Assert
        verify(kafkaProducer, times(1)).send(anyString(), anyString(), any(PaymentRequestAvroModel.class), any());
    }
}