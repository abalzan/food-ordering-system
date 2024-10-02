package com.andrei.food.ordering.system.kafka.producer;

import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.exception.OrderDomainException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.SendResult;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaMessageHelperTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private SendResult<String, String> sendResult;
    @Mock
    private RecordMetadata recordMetadata;

    private KafkaMessageHelper kafkaMessageHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaMessageHelper = new KafkaMessageHelper(objectMapper);
    }

    @Test
    @DisplayName("Should log success and update outbox status to COMPLETED when message is sent successfully")
    void shouldLogSuccessAndUpdateOutboxStatusToCompletedWhenMessageIsSentSuccessfully() {
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.topic()).thenReturn("testTopic");
        when(recordMetadata.partition()).thenReturn(1);
        when(recordMetadata.offset()).thenReturn(1L);
        when(recordMetadata.timestamp()).thenReturn(1L);

        BiConsumer<String, OutboxStatus> outboxCallback = mock(BiConsumer.class);
        BiConsumer<SendResult<String, String>, Throwable> callback = kafkaMessageHelper.getKafkaCallback("testTopic", "testMessage", "testOutboxMessage", outboxCallback, "testOrderId", "testAvroModel");

        callback.accept(sendResult, null);

        verify(outboxCallback, times(1)).accept("testOutboxMessage", OutboxStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should log error and update outbox status to FAILED when message sending fails")
    void shouldLogErrorAndUpdateOutboxStatusToFailedWhenMessageSendingFails() {
        Throwable ex = new RuntimeException("test exception");

        BiConsumer<String, OutboxStatus> outboxCallback = mock(BiConsumer.class);
        BiConsumer<SendResult<String, String>, Throwable> callback = kafkaMessageHelper.getKafkaCallback("testTopic", "testMessage", "testOutboxMessage", outboxCallback, "testOrderId", "testAvroModel");

        callback.accept(null, ex);

        verify(outboxCallback, times(1)).accept("testOutboxMessage", OutboxStatus.FAILED);
    }

    @Test
    @DisplayName("Should deserialize payload to specified class successfully")
    void shouldDeserializePayloadToSpecifiedClassSuccessfully() throws Exception {
        String payload = "{\"key\":\"value\"}";
        TestClass expectedObject = new TestClass("value");
        when(objectMapper.readValue(payload, TestClass.class)).thenReturn(expectedObject);

        TestClass result = kafkaMessageHelper.getOrderEventPayload(payload, TestClass.class);

        assertEquals(expectedObject, result);
    }

    @Test
    @DisplayName("Should throw OrderDomainException when deserialization fails")
    void shouldThrowOrderDomainExceptionWhenDeserializationFails() throws Exception {
        String payload = "{\"key\":\"value\"}";
        when(objectMapper.readValue(payload, TestClass.class)).thenThrow(new RuntimeException("test exception"));

        assertThrows(OrderDomainException.class, () -> kafkaMessageHelper.getOrderEventPayload(payload, TestClass.class));
    }

    static class TestClass {
        private String key;

        public TestClass(String key) {
            this.key = key;
        }

        // getters and setters
    }
}