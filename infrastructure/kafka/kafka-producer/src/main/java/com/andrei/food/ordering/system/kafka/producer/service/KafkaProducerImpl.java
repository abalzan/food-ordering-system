package com.andrei.food.ordering.system.kafka.producer.service;

import com.andrei.food.ordering.system.kafka.producer.exception.KafkaProducerException;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.Serializable;

@Slf4j
@Component
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, K key, V message, ListenableFutureCallback<SendResult<K, V>> callback) {
        log.info("Sending message='{}' to topic='{}'", message, topicName);
        try {
            kafkaTemplate.send(topicName, key, message).addCallback(callback);
        } catch (KafkaException e) {
            log.error("Error sending message='{}' to topic='{}'", message, topicName, e);
            throw new KafkaProducerException("Error sending message= "+message+" to topic=" +topicName);
        }
    }

    @PreDestroy
    public void close() {
        if(kafkaTemplate != null) {
            kafkaTemplate.destroy();
        }
    }
}
