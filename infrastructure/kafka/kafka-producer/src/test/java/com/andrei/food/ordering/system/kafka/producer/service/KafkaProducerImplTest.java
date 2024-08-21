package com.andrei.food.ordering.system.kafka.producer.service;

import com.andrei.food.ordering.system.kafka.producer.exception.KafkaProducerException;
import org.apache.avro.specific.SpecificRecordBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;
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
    void sendMessageSuccessfully() {
        String topicName = "test-topic";
        Serializable key = "test-key";
        SpecificRecordBase message = mock(SpecificRecordBase.class);
        BiConsumer<SendResult<Serializable, SpecificRecordBase>, Throwable> callback = mock(BiConsumer.class);
        CompletableFuture<SendResult<Serializable, SpecificRecordBase>> future = mock(CompletableFuture.class);

        when(kafkaTemplate.send(topicName, key, message)).thenReturn(future);

        kafkaProducer.send(topicName, key, message, callback);

        verify(kafkaTemplate).send(topicName, key, message);
        verify(future).whenComplete(callback);
    }

    @Test
    void sendMessageThrowsKafkaProducerException() {
        String topicName = "test-topic";
        Serializable key = "test-key";
        SpecificRecordBase message = mock(SpecificRecordBase.class);
        BiConsumer<SendResult<Serializable, SpecificRecordBase>, Throwable> callback = mock(BiConsumer.class);

        when(kafkaTemplate.send(topicName, key, message)).thenThrow(KafkaException.class);

        assertThrows(KafkaProducerException.class, () -> kafkaProducer.send(topicName, key, message, callback));
    }

    @Test
    void closeKafkaTemplateSuccessfully() {
        kafkaProducer.close();

        verify(kafkaTemplate).destroy();
    }

    @Test
    void closeKafkaTemplateWhenNull() {
        KafkaProducerImpl<Serializable, SpecificRecordBase> kafkaProducerWithNullTemplate = new KafkaProducerImpl<>(null);

        assertDoesNotThrow(kafkaProducerWithNullTemplate::close);
    }
}