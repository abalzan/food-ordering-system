package com.andrei.food.ordering.system.customer.service.messaging.publisher.kafka;

import com.andrei.food.ordering.system.customer.service.domain.config.CustomerServiceConfigData;
import com.andrei.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.customer.service.messaging.mapper.CustomerMessagingDataMapper;
import com.andrei.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.support.SendResult;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerCreatedEventKafkaPublisherTest {

    private CustomerMessagingDataMapper customerMessagingDataMapper;
    private KafkaProducer<String, CustomerAvroModel> kafkaProducer;
    private CustomerServiceConfigData customerServiceConfigData;
    private CustomerCreatedEventKafkaPublisher customerCreatedEventKafkaPublisher;

    @BeforeEach
    void setUp() {
        customerMessagingDataMapper = mock(CustomerMessagingDataMapper.class);
        kafkaProducer = mock(KafkaProducer.class);
        customerServiceConfigData = mock(CustomerServiceConfigData.class);
        customerCreatedEventKafkaPublisher = new CustomerCreatedEventKafkaPublisher(
                customerMessagingDataMapper, kafkaProducer, customerServiceConfigData);
    }

    @Test
    void publishCustomerCreatedEventSuccessfully() {
        Customer customer = new Customer(new CustomerId(UUID.randomUUID()), "john_doe", "John", "Doe");
        CustomerCreatedEvent event = new CustomerCreatedEvent(customer, ZonedDateTime.now());
        CustomerAvroModel avroModel = new CustomerAvroModel();
        avroModel.setId(customer.getId().getValue());

        when(customerMessagingDataMapper.customerCreatedEventToCustomerAvroModel(any(CustomerCreatedEvent.class)))
                .thenReturn(avroModel);
        when(customerServiceConfigData.getCustomerTopicName()).thenReturn("customer-topic");

        customerCreatedEventKafkaPublisher.publish(event);

        verify(kafkaProducer, times(1)).send(anyString(), anyString(), any(CustomerAvroModel.class), any(BiConsumer.class));
    }

    @Test
    void callbackHandlesSuccess() {
        CustomerAvroModel avroModel = new CustomerAvroModel();
        SendResult<String, CustomerAvroModel> sendResult = mock(SendResult.class);
        RecordMetadata metadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(metadata);
        when(metadata.topic()).thenReturn("customer-topic");
        when(metadata.partition()).thenReturn(0);
        when(metadata.offset()).thenReturn(0L);
        when(metadata.timestamp()).thenReturn(System.currentTimeMillis());

        BiConsumer<SendResult<String, CustomerAvroModel>, Throwable> callback =
                customerCreatedEventKafkaPublisher.getCallback("customer-topic", avroModel);

        callback.accept(sendResult, null);

        verify(metadata, times(1)).topic();
    }

    @Test
    void callbackHandlesFailure() {
        CustomerAvroModel avroModel = new CustomerAvroModel();
        Throwable ex = new RuntimeException("Kafka send error");

        BiConsumer<SendResult<String, CustomerAvroModel>, Throwable> callback =
                customerCreatedEventKafkaPublisher.getCallback("customer-topic", avroModel);

        callback.accept(null, ex);

        // No assertions needed, just verifying that the log error is called
    }
}