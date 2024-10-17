package com.andrei.food.ordering.system.customer.service.messaging.publisher.kafka;

import com.andrei.food.ordering.system.customer.service.domain.config.CustomerServiceConfigData;
import com.andrei.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.andrei.food.ordering.system.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import com.andrei.food.ordering.system.customer.service.messaging.mapper.CustomerMessagingDataMapper;
import com.andrei.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@AllArgsConstructor
@Component
public class CustomerCreatedEventKafkaPublisher implements CustomerMessagePublisher {

    private final CustomerMessagingDataMapper customerMessagingDataMapper;

    private final KafkaProducer<String, CustomerAvroModel> kafkaProducer;

    private final CustomerServiceConfigData customerServiceConfigData;

    @Override
    public void publish(CustomerCreatedEvent customerCreatedEvent) {
        log.info("Received CustomerCreatedEvent for customer id: {}",
                customerCreatedEvent.getCustomer().getId().getValue());
        try {
            CustomerAvroModel customerAvroModel = customerMessagingDataMapper
                    .customerCreatedEventToCustomerAvroModel(customerCreatedEvent);

            kafkaProducer.send(customerServiceConfigData.getCustomerTopicName(), customerAvroModel.getId().toString(),
                    customerAvroModel,
                    getCallback(customerServiceConfigData.getCustomerTopicName(), customerAvroModel));

            log.info("CustomerCreatedEvent sent to kafka for customer id: {}",
                    customerAvroModel.getId());
        } catch (Exception e) {
            log.error("Error while sending CustomerCreatedEvent to kafka for customer id: {}," +
                    " error: {}", customerCreatedEvent.getCustomer().getId().getValue(), e.getMessage());
        }
    }

    protected BiConsumer<SendResult<String, CustomerAvroModel>, Throwable>
    getCallback(String topicName, CustomerAvroModel message) {
        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending message {} to topic {}", message.toString(), topicName, ex);
            } else {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp(),
                        System.nanoTime());
            }
        };
    }
}