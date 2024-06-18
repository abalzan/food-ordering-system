package com.andrei.food.ordering.system.kafka.producer.service;

import com.andrei.food.ordering.system.kafka.producer.exception.KafkaProducerException;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaProducerImplTest {

    @Mock
    private KafkaTemplate<Serializable, SpecificRecordBase> kafkaTemplate;

    private KafkaProducerImpl<Serializable, SpecificRecordBase> kafkaProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaProducer = new KafkaProducerImpl<>(kafkaTemplate);
    }

    @Test
    void shouldSendMessageSuccessfully() {
        // Given
        String topicName = "testTopic";
        Serializable key = "testKey";
        SpecificRecordBase message = mock(SpecificRecordBase.class);
        ListenableFuture<SendResult<Serializable, SpecificRecordBase>> future = mock(ListenableFuture.class);
        ListenableFutureCallback<SendResult<Serializable, SpecificRecordBase>> callback = mock(ListenableFutureCallback.class);

        // When
        when(kafkaTemplate.send(topicName, key, message)).thenReturn(future);

        // Then
        kafkaProducer.send(topicName, key, message, callback);
        verify(kafkaTemplate, times(1)).send(topicName, key, message);
        verify(future, times(1)).addCallback(callback);
    }

    @Test
    void shouldThrowKafkaProducerExceptionWhenSendMessageFails() {
        // Given
        String topicName = "testTopic";
        Serializable key = "testKey";
        SpecificRecordBase message = mock(SpecificRecordBase.class);
        ListenableFutureCallback<SendResult<Serializable, SpecificRecordBase>> callback = mock(ListenableFutureCallback.class);

        // When
        when(kafkaTemplate.send(any(), any(), any())).thenThrow(new KafkaProducerException("Test Error Message"));

        // Then
        assertThrows(KafkaProducerException.class, () -> kafkaProducer.send(topicName, key, message, callback));
    }
}
