package com.andrei.food.ordering.system.payment.service.messaging.publisher.kafka;

import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.andrei.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import com.andrei.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import com.andrei.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.util.function.BiConsumer;
import com.andrei.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.Assert;

class PaymentEventKafkaPublisherTest {

    @InjectMocks
    private PaymentEventKafkaPublisher paymentEventKafkaPublisher;
    @Mock
    private PaymentMessagingDataMapper paymentMessagingDataMapper;
    @Mock
    private KafkaProducer kafkaProducer;
    @Mock
    private PaymentServiceConfigData paymentServiceConfigData;
    @Mock
    private KafkaMessageHelper kafkaMessageHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Publishes payment response successfully")
    void publishesPaymentResponseSuccessfully() {
        OrderOutboxMessage orderOutboxMessage = mock(OrderOutboxMessage.class);
        OrderEventPayload orderEventPayload = OrderEventPayload.builder()
                .orderId(UUID.randomUUID().toString())
                .build();
        when(orderOutboxMessage.getPayload()).thenReturn(orderEventPayload.toString());
        when(orderOutboxMessage.getSagaId()).thenReturn(UUID.randomUUID());
        when(kafkaMessageHelper.getOrderEventPayload(anyString(), any())).thenReturn(orderEventPayload);

        PaymentResponseAvroModel paymentResponseAvroModel = mock(PaymentResponseAvroModel.class);
        when(paymentMessagingDataMapper.orderEventPayloadToPaymentResponseAvroModel(anyString(), any())).thenReturn(paymentResponseAvroModel);
        when(paymentServiceConfigData.getPaymentResponseTopicName()).thenReturn("payment-response-topic");

        BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback = mock(BiConsumer.class);

        paymentEventKafkaPublisher.publish(orderOutboxMessage, outboxCallback);

        verify(kafkaProducer).send(eq("payment-response-topic"), anyString(), eq(paymentResponseAvroModel), any());
        verify(outboxCallback, never()).accept(any(), eq(OutboxStatus.FAILED));
    }

    @Test
    @DisplayName("Handles exception during payment response publishing")
    void handlesExceptionDuringPaymentResponsePublishing() {
        UUID sagaId = UUID.randomUUID();
        LogCaptor logCaptor = LogCaptor.forClass(PaymentEventKafkaPublisher.class);
        OrderOutboxMessage orderOutboxMessage = mock(OrderOutboxMessage.class);
        OrderEventPayload orderEventPayload = OrderEventPayload.builder()
                .orderId(UUID.randomUUID().toString())
                .build();
        when(orderOutboxMessage.getPayload()).thenReturn(orderEventPayload.toString());
        when(orderOutboxMessage.getSagaId()).thenReturn(sagaId);
        when(kafkaMessageHelper.getOrderEventPayload(anyString(), any())).thenReturn(orderEventPayload);

        when(paymentMessagingDataMapper.orderEventPayloadToPaymentResponseAvroModel(anyString(), any())).thenThrow(new RuntimeException("Test exception"));

        BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback = mock(BiConsumer.class);

        paymentEventKafkaPublisher.publish(orderOutboxMessage, outboxCallback);

        verify(kafkaProducer, never()).send(anyString(), anyString(), any(), any());
        verify(outboxCallback).accept(orderOutboxMessage, OutboxStatus.FAILED);
        Assertions.assertTrue(logCaptor.getErrorLogs().contains("Error occurred while sending PaymentResponseAvroModel for order with id " + orderEventPayload.getOrderId() + " and saga id " + sagaId + ". Error: Test exception"));
    }

    @Test
    @DisplayName("Publishes payment response with correct topic name")
    void publishesPaymentResponseWithCorrectTopicName() {
        OrderOutboxMessage orderOutboxMessage = mock(OrderOutboxMessage.class);
        OrderEventPayload orderEventPayload = OrderEventPayload.builder()
                .orderId(UUID.randomUUID().toString())
                .build();
        when(orderOutboxMessage.getPayload()).thenReturn(orderEventPayload.toString());
        when(orderOutboxMessage.getSagaId()).thenReturn(UUID.randomUUID());
        when(kafkaMessageHelper.getOrderEventPayload(anyString(), any())).thenReturn(orderEventPayload);

        PaymentResponseAvroModel paymentResponseAvroModel = mock(PaymentResponseAvroModel.class);
        when(paymentMessagingDataMapper.orderEventPayloadToPaymentResponseAvroModel(anyString(), any())).thenReturn(paymentResponseAvroModel);
        when(paymentServiceConfigData.getPaymentResponseTopicName()).thenReturn("payment-response-topic");

        BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback = mock(BiConsumer.class);

        paymentEventKafkaPublisher.publish(orderOutboxMessage, outboxCallback);

        verify(kafkaProducer).send(eq("payment-response-topic"), anyString(), eq(paymentResponseAvroModel), any());
        verify(outboxCallback, never()).accept(any(), eq(OutboxStatus.FAILED));
    }
}