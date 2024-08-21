package com.andrei.food.ordering.system.payment.service.messaging.publisher.kafka;

import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.domain.event.PaymentCompletedEvent;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.andrei.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import com.andrei.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import nl.altindag.log.LogCaptor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

class PaymentCompletedKafkaMessagePublisherTest {

    @Mock
    private PaymentMessagingDataMapper paymentMessagingDataMapper;

    @Mock
    private KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;

    @Mock
    private PaymentServiceConfigData paymentServiceConfigData;

    @Mock
    private KafkaMessageHelper kafkaMessageHelper;

    @Mock
    private Logger logger;

    @InjectMocks
    private PaymentCompletedKafkaMessagePublisher paymentCompletedKafkaMessagePublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentCompletedEventIsPublishedSuccessfully() {
        PaymentCompletedEvent paymentCompletedEvent = mock(PaymentCompletedEvent.class);
        Payment payment = Payment.builder().orderId(new OrderId(UUID.randomUUID())).build();
        PaymentResponseAvroModel paymentResponseAvroModel = PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setPaymentId(UUID.randomUUID())
                .setCustomerId(UUID.randomUUID())
                .setOrderId(UUID.randomUUID())
                .setPrice(new BigDecimal("100.0"))
                .setCreatedAt(Instant.now())
                .setPaymentStatus(PaymentStatus.COMPLETED)
                .setFailureMessages(Collections.emptyList())
                .build();

        when(paymentCompletedEvent.getPayment()).thenReturn(payment);
        when(paymentMessagingDataMapper.paymentCompletedEventToPaymentResponseAvroModel(paymentCompletedEvent)).thenReturn(paymentResponseAvroModel);

        paymentCompletedKafkaMessagePublisher.publish(paymentCompletedEvent);

        verify(kafkaProducer).send(
                eq(paymentServiceConfigData.getPaymentResponseTopicName()),
                anyString(),
                eq(paymentResponseAvroModel),
                any()
        );
    }

    @Test
    void paymentCompletedEventPublishHandlesException() {
        LogCaptor logCaptor = LogCaptor.forClass(PaymentCompletedKafkaMessagePublisher.class);
        UUID orderUUID = UUID.randomUUID();
        PaymentCompletedEvent paymentCompletedEvent = mock(PaymentCompletedEvent.class);
        Payment payment = Payment.builder().orderId(new OrderId(orderUUID)).build();


        when(paymentCompletedEvent.getPayment()).thenReturn(payment);
        when(paymentMessagingDataMapper.paymentCompletedEventToPaymentResponseAvroModel(paymentCompletedEvent)).thenThrow(new RuntimeException("Test exception"));

        paymentCompletedKafkaMessagePublisher.publish(paymentCompletedEvent);

        verify(kafkaProducer, never()).send(anyString(), anyString(), any(), any());

        Assertions.assertThat(logCaptor.getErrorLogs().get(0)).contains("Error while sending PaymentResponseAvroModel message to kafka with order id " + orderUUID + ", error Test exception");
    }
}