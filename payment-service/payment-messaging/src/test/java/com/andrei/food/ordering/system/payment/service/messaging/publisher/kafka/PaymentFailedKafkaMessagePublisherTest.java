package com.andrei.food.ordering.system.payment.service.messaging.publisher.kafka;

import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.domain.event.PaymentFailedEvent;
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


class PaymentFailedKafkaMessagePublisherTest {

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
    private PaymentFailedKafkaMessagePublisher paymentFailedKafkaMessagePublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentFailedEventIsPublishedSuccessfully() {
        PaymentFailedEvent paymentFailedEvent = mock(PaymentFailedEvent.class);
        Payment payment = Payment.builder().orderId(new OrderId(UUID.randomUUID())).build();
        PaymentResponseAvroModel paymentResponseAvroModel = PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setPaymentId(UUID.randomUUID())
                .setCustomerId(UUID.randomUUID())
                .setOrderId(UUID.randomUUID())
                .setPrice(new BigDecimal("100.0"))
                .setCreatedAt(Instant.now())
                .setPaymentStatus(PaymentStatus.FAILED)
                .setFailureMessages(Collections.singletonList("Test failure"))
                .build();

        when(paymentFailedEvent.getPayment()).thenReturn(payment);
        when(paymentMessagingDataMapper.paymentFailedEventToPaymentResponseAvroModel(paymentFailedEvent)).thenReturn(paymentResponseAvroModel);

        paymentFailedKafkaMessagePublisher.publish(paymentFailedEvent);

        verify(kafkaProducer).send(
                eq(paymentServiceConfigData.getPaymentResponseTopicName()),
                anyString(),
                eq(paymentResponseAvroModel),
                any()
        );
    }

    @Test
    void paymentFailedEventPublishHandlesException() {
        LogCaptor logCaptor = LogCaptor.forClass(PaymentFailedKafkaMessagePublisher.class);
        UUID orderUUID = UUID.randomUUID();
        PaymentFailedEvent paymentFailedEvent = mock(PaymentFailedEvent.class);
        Payment payment = Payment.builder().orderId(new OrderId(orderUUID)).build();

        when(paymentFailedEvent.getPayment()).thenReturn(payment);
        when(paymentMessagingDataMapper.paymentFailedEventToPaymentResponseAvroModel(paymentFailedEvent)).thenThrow(new RuntimeException("Test exception"));

        paymentFailedKafkaMessagePublisher.publish(paymentFailedEvent);

        verify(kafkaProducer, never()).send(anyString(), anyString(), any(), any());

        Assertions.assertThat(logCaptor.getErrorLogs())
                .contains("Error while sending PaymentResponseAvroModel message to kafka with order id " + orderUUID + ", error Test exception");
       }
}