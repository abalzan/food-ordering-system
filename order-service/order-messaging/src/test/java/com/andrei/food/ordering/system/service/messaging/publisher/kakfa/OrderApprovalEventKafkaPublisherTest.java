package com.andrei.food.ordering.system.service.messaging.publisher.kakfa;

import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.andrei.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.service.domain.config.OrderServiceConfigData;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
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

class OrderApprovalEventKafkaPublisherTest {

    @Mock
    private OrderServiceConfigData orderServiceConfigData;
    @Mock
    private KafkaMessageHelper kafkaMessageHelper;

    @Mock
    private OrderApprovalEventPayload orderApprovalEventPayload;

    @Mock
    private OrderMessagingDataMapper orderMessagingDataMapper;

    @Mock
    private BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback;

    @Mock
    private OrderApprovalOutboxMessage orderApprovalOutboxMessage;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private OrderApprovalEventKafkaPublisher orderApprovalEventKafkaPublisher;

    private UUID sagaId;
    private String payload;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sagaId = UUID.randomUUID();
        payload = "{ \"orderId\": \"12345\" }"; // Example payload
        when(orderApprovalOutboxMessage.getSagaId()).thenReturn(sagaId);
        when(orderApprovalOutboxMessage.getPayload()).thenReturn(payload);
    }

    @Test
    @DisplayName("Publishes OrderApprovalEvent successfully")
    void publishesOrderApprovalEventSuccessfully() {
        when(kafkaMessageHelper.getOrderEventPayload(anyString(), eq(OrderApprovalEventPayload.class)))
                .thenReturn(orderApprovalEventPayload);
        when(orderMessagingDataMapper.orderApprovalEventToRestaurantApprovalRequestAvroModel(anyString(), any(OrderApprovalEventPayload.class)))
                .thenReturn(new RestaurantApprovalRequestAvroModel());
        when(orderServiceConfigData.getRestaurantApprovalRequestTopicName()).thenReturn("test-topic");

        orderApprovalEventKafkaPublisher.publish(orderApprovalOutboxMessage, outboxCallback);

        verify(kafkaProducer, times(1)).send(anyString(), anyString(), any(RestaurantApprovalRequestAvroModel.class), any());
        verify(outboxCallback, never()).accept(any(OrderApprovalOutboxMessage.class), any(OutboxStatus.class));
    }

    @Test
    @DisplayName("Handles exception during OrderApprovalEvent publishing")
    void handlesExceptionDuringOrderApprovalEventPublishing() {
        when(kafkaMessageHelper.getOrderEventPayload(anyString(), eq(OrderApprovalEventPayload.class)))
                .thenReturn(orderApprovalEventPayload);
        when(orderMessagingDataMapper.orderApprovalEventToRestaurantApprovalRequestAvroModel(anyString(), any(OrderApprovalEventPayload.class)))
                .thenThrow(new RuntimeException("Test exception"));

        orderApprovalEventKafkaPublisher.publish(orderApprovalOutboxMessage, outboxCallback);

        verify(kafkaProducer, never()).send(anyString(), anyString(), any(RestaurantApprovalRequestAvroModel.class), any());
        verify(outboxCallback, never()).accept(any(OrderApprovalOutboxMessage.class), any(OutboxStatus.class));
    }

    @Test
    @DisplayName("Logs error when KafkaProducer send fails")
    void logsErrorWhenKafkaProducerSendFails() {
        when(kafkaMessageHelper.getOrderEventPayload(anyString(), eq(OrderApprovalEventPayload.class)))
                .thenReturn(orderApprovalEventPayload);
        when(orderMessagingDataMapper.orderApprovalEventToRestaurantApprovalRequestAvroModel(anyString(), any(OrderApprovalEventPayload.class)))
                .thenReturn(new RestaurantApprovalRequestAvroModel());

        doThrow(new RuntimeException("Kafka send failed")).when(kafkaProducer).send(anyString(), anyString(), any(RestaurantApprovalRequestAvroModel.class), any());

        orderApprovalEventKafkaPublisher.publish(orderApprovalOutboxMessage, outboxCallback);

        verify(outboxCallback, never()).accept(any(OrderApprovalOutboxMessage.class), any(OutboxStatus.class));
    }
}