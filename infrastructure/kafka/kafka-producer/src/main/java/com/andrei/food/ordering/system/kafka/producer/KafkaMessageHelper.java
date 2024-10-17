package com.andrei.food.ordering.system.kafka.producer;

import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.exception.OrderDomainException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;

    public <T, U> BiConsumer<SendResult<String, T>, Throwable>
    getKafkaCallback(String responseTopicName, T requestAvroModel, U outboxMessage,
                     BiConsumer<U, OutboxStatus> outboxCallback,
                     String orderId, String avroModelName) {
        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending {} message to kafka topic: {} with message: {} for orderId: {}", avroModelName, responseTopicName, requestAvroModel, orderId, ex);
                outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
            } else {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successful response from kafka for orderId: {} " +
                                "Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());

                outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
            }
        };
    }


    public <T> T getOrderEventPayload(String payload, Class<T> outputTypeClass) {
        try {
            return objectMapper.readValue(payload, outputTypeClass);
        } catch (Exception e) {
            log.error("Error while deserializing {} ", outputTypeClass.getName(), e);
            throw new OrderDomainException("Error while deserializing "+ outputTypeClass.getName(), e);
        }
    }
}
