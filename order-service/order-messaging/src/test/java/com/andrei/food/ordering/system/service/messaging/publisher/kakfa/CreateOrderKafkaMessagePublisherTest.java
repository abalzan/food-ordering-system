package com.andrei.food.ordering.system.service.messaging.publisher.kakfa;

import com.andrei.food.ordering.system.service.domain.config.OrderServiceConfigData;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.event.OrderCreatedEvent;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateOrderKafkaMessagePublisherTest {

    @Mock
    private OrderMessagingDataMapper orderMessagingDataMapper;
    @Mock
    private OrderServiceConfigData orderServiceConfigData;
    @Mock
    private OrderKafkaMessageHelper orderKafkaMessageHelper;
    @Mock
    private KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    @Mock
    private OrderCreatedEvent orderCreatedEvent;
    @Mock
    private Order order;
    @Mock
    private OrderId orderId;

    private CreateOrderKafkaMessagePublisher createOrderKafkaMessagePublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(orderCreatedEvent.getOrder()).thenReturn(order);
        when(order.getId()).thenReturn(orderId);
        when(orderId.getValue()).thenReturn(java.util.UUID.randomUUID());
        createOrderKafkaMessagePublisher = new CreateOrderKafkaMessagePublisher(orderMessagingDataMapper, orderServiceConfigData, kafkaProducer, orderKafkaMessageHelper);
    }

    @Test
    @DisplayName("Should publish order created event successfully")
    void shouldPublishOrderCreatedEventSuccessfully() {
        when(orderMessagingDataMapper.orderCreatedEventToPaymentRequestAvroModel(any(OrderCreatedEvent.class))).thenReturn(new PaymentRequestAvroModel());
        when(orderServiceConfigData.getPaymentRequestTopicName()).thenReturn("testTopic");
        doNothing().when(kafkaProducer).send(anyString(), anyString(), any(PaymentRequestAvroModel.class), any());

        createOrderKafkaMessagePublisher.publish(orderCreatedEvent);
        // Assert
        verify(kafkaProducer, times(1)).send(anyString(), anyString(), any(PaymentRequestAvroModel.class), any());
    }

    @Test
    @DisplayName("Should handle exception when publishing order created event")
    void shouldHandleExceptionWhenPublishingOrderCreatedEvent() {
        when(orderMessagingDataMapper.orderCreatedEventToPaymentRequestAvroModel(any(OrderCreatedEvent.class))).thenReturn(new PaymentRequestAvroModel());
        when(orderServiceConfigData.getPaymentRequestTopicName()).thenReturn("testTopic");
        doThrow(new RuntimeException()).when(kafkaProducer).send(anyString(), anyString(), any(PaymentRequestAvroModel.class), any());

        createOrderKafkaMessagePublisher.publish(orderCreatedEvent);

        // Assert
        verify(kafkaProducer, times(1)).send(anyString(), anyString(), any(PaymentRequestAvroModel.class), any());
    }
}