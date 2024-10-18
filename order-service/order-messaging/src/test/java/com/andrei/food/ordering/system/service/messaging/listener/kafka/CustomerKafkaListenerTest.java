package com.andrei.food.ordering.system.service.messaging.listener.kafka;

import com.andrei.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import com.andrei.food.ordering.system.service.domain.ports.input.message.listener.customer.CustomerMessageListener;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class CustomerKafkaListenerTest {

    @Mock
    private CustomerMessageListener customerMessageListener;

    @Mock
    private OrderMessagingDataMapper orderMessagingDataMapper;

    @InjectMocks
    private CustomerKafkaListener customerKafkaListener;

    private CustomerAvroModel customerAvroModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerAvroModel = new CustomerAvroModel(UUID.randomUUID(), "john_doe", "John", "Doe");
    }

    @Test
    void receivesAndProcessesCustomerMessagesSuccessfully() {
        List<CustomerAvroModel> messages = Collections.singletonList(customerAvroModel);
        List<String> keys = Collections.singletonList("key");
        List<Integer> partitions = Collections.singletonList(0);
        List<Long> offsets = Collections.singletonList(0L);

        customerKafkaListener.receive(messages, keys, partitions, offsets);

        verify(customerMessageListener, times(1)).customerCreated(any());
    }

    @Test
    void handlesExceptionDuringMessageProcessing() {
        List<CustomerAvroModel> messages = Collections.singletonList(customerAvroModel);
        List<String> keys = Collections.singletonList("key");
        List<Integer> partitions = Collections.singletonList(0);
        List<Long> offsets = Collections.singletonList(0L);

        doThrow(new RuntimeException("Exception occurred")).when(customerMessageListener).customerCreated(any());

        customerKafkaListener.receive(messages, keys, partitions, offsets);

        verify(customerMessageListener, times(1)).customerCreated(any());
    }

    @Test
    void processesEmptyMessageList() {
        List<CustomerAvroModel> messages = Collections.emptyList();
        List<String> keys = Collections.emptyList();
        List<Integer> partitions = Collections.emptyList();
        List<Long> offsets = Collections.emptyList();

        customerKafkaListener.receive(messages, keys, partitions, offsets);

        verify(customerMessageListener, never()).customerCreated(any());
    }
}