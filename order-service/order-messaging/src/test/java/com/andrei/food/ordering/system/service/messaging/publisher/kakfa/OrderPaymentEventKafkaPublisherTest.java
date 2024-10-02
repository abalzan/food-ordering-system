package com.andrei.food.ordering.system.service.messaging.publisher.kakfa;

import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.andrei.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.service.domain.config.OrderServiceConfigData;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;
import java.util.function.BiConsumer;

class OrderPaymentEventKafkaPublisherTest {

    @Mock
    private OrderServiceConfigData orderServiceConfigData;
    @Mock
    private KafkaMessageHelper kafkaMessageHelper;
    @Mock
    private OrderPaymentEventPayload orderPaymentEventPayload;
    @Mock
    private OrderMessagingDataMapper orderMessagingDataMapper;
    @Mock
    private BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback;
    @Mock
    private OrderPaymentOutboxMessage orderPaymentOutboxMessage;
    @Mock
    private KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;

    @InjectMocks
    private OrderPaymentEventKafkaPublisher orderPaymentEventKafkaPublisher;

    private UUID sagaId;
    private String payload;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sagaId = UUID.randomUUID();
        payload = "{ \"orderId\": \"12345\" }"; // Example payload
        when(orderPaymentOutboxMessage.getSagaId()).thenReturn(sagaId);
        when(orderPaymentOutboxMessage.getPayload()).thenReturn(payload);
    }

    @Test
    @DisplayName("Publishes OrderPaymentEvent successfully")
    void publishesOrderPaymentEventSuccessfully() {
        when(kafkaMessageHelper.getOrderEventPayload(anyString(), eq(OrderPaymentEventPayload.class)))
                .thenReturn(orderPaymentEventPayload);
        when(orderMessagingDataMapper.orderPaymentEventToPaymentRequestAvroModel(anyString(), any(OrderPaymentEventPayload.class)))
                .thenReturn(new PaymentRequestAvroModel());
        when(orderServiceConfigData.getPaymentRequestTopicName()).thenReturn("test-topic");

        orderPaymentEventKafkaPublisher.publish(orderPaymentOutboxMessage, outboxCallback);

        verify(kafkaProducer, times(1)).send(anyString(), anyString(), any(PaymentRequestAvroModel.class), any());
        verify(outboxCallback, never()).accept(any(OrderPaymentOutboxMessage.class), any(OutboxStatus.class));
    }

    @Test
    @DisplayName("Handles exception during OrderPaymentEvent publishing")
    void handlesExceptionDuringOrderPaymentEventPublishing() {
        when(kafkaMessageHelper.getOrderEventPayload(anyString(), eq(OrderPaymentEventPayload.class)))
                .thenReturn(orderPaymentEventPayload);
        when(orderMessagingDataMapper.orderPaymentEventToPaymentRequestAvroModel(anyString(), any(OrderPaymentEventPayload.class)))
                .thenThrow(new RuntimeException("Test exception"));

        orderPaymentEventKafkaPublisher.publish(orderPaymentOutboxMessage, outboxCallback);

        verify(kafkaProducer, never()).send(anyString(), anyString(), any(PaymentRequestAvroModel.class), any());
        verify(outboxCallback, never()).accept(any(OrderPaymentOutboxMessage.class), any(OutboxStatus.class));
    }

    @Test
    @DisplayName("Logs error when KafkaProducer send fails")
    void logsErrorWhenKafkaProducerSendFails() {
        when(kafkaMessageHelper.getOrderEventPayload(anyString(), eq(OrderPaymentEventPayload.class)))
                .thenReturn(orderPaymentEventPayload);
        when(orderMessagingDataMapper.orderPaymentEventToPaymentRequestAvroModel(anyString(), any(OrderPaymentEventPayload.class)))
                .thenReturn(new PaymentRequestAvroModel());

        doThrow(new RuntimeException("Kafka send failed")).when(kafkaProducer).send(anyString(), anyString(), any(PaymentRequestAvroModel.class), any());

        orderPaymentEventKafkaPublisher.publish(orderPaymentOutboxMessage, outboxCallback);

        verify(outboxCallback, never()).accept(any(OrderPaymentOutboxMessage.class), any(OutboxStatus.class));
    }
}