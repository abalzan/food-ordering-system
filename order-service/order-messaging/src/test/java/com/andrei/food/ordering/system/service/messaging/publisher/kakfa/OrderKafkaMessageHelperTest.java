package com.andrei.food.ordering.system.service.messaging.publisher.kakfa;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.SendResult;

import static org.mockito.Mockito.*;

class OrderKafkaMessageHelperTest {

    @Mock
    private SendResult<String, String> sendResult;
    @Mock
    private RecordMetadata recordMetadata;

    private OrderKafkaMessageHelper orderKafkaMessageHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderKafkaMessageHelper = new OrderKafkaMessageHelper();
    }

    @Test
    @DisplayName("Should log success when message is sent successfully")
    void shouldLogSuccessWhenMessageIsSentSuccessfully() {
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.topic()).thenReturn("testTopic");
        when(recordMetadata.partition()).thenReturn(1);
        when(recordMetadata.offset()).thenReturn(1L);
        when(recordMetadata.timestamp()).thenReturn(1L);

        orderKafkaMessageHelper.getKafkaCallback("testTopic", "testMessage", "testOrderId", "testMessageName").onSuccess(sendResult);
    }

    @Test
    @DisplayName("Should log error when message sending fails")
    void shouldLogErrorWhenMessageSendingFails() {
        Throwable ex = new RuntimeException("test exception");

        orderKafkaMessageHelper.getKafkaCallback("testTopic", "testMessage", "testOrderId", "testMessageName").onFailure(ex);
    }
}