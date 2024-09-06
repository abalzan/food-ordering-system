package com.andrei.food.ordering.system.kafka.consumer.config;

import com.andrei.food.ordering.system.kafka.config.data.KafkaConfigData;
import com.andrei.food.ordering.system.kafka.config.data.KafkaConsumerConfigData;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.io.Serializable;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class KafkaConsumerConfigTest {

    @Mock
    private KafkaConfigData kafkaConfigData;

    @Mock
    private KafkaConsumerConfigData kafkaConsumerConfigData;

    private KafkaConsumerConfig<Serializable, SpecificRecordBase> kafkaConsumerConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaConsumerConfig = new KafkaConsumerConfig<>(kafkaConfigData, kafkaConsumerConfigData);
        when(kafkaConfigData.getBootstrapServers()).thenReturn("localhost:9092");
        when(kafkaConsumerConfigData.getKeyDeserializer()).thenReturn("org.apache.kafka.common.serialization.StringDeserializer");
        when(kafkaConsumerConfigData.getValueDeserializer()).thenReturn("io.confluent.kafka.serializers.KafkaAvroDeserializer");
        when(kafkaConsumerConfigData.getAutoOffsetReset()).thenReturn("earliest");
        when(kafkaConfigData.getSchemaRegistryUrlKey()).thenReturn("schema.registry.url");
        when(kafkaConfigData.getSchemaRegistryUrl()).thenReturn("http://localhost:8081");
        when(kafkaConsumerConfigData.getSpecificAvroReader()).thenReturn("true");
        when(kafkaConsumerConfigData.getSpecificAvroReaderKey()).thenReturn("specific.avro.reader");
        when(kafkaConsumerConfigData.getSessionTimeoutMs()).thenReturn(15000);
        when(kafkaConsumerConfigData.getHeartbeatIntervalMs()).thenReturn(3000);
        when(kafkaConsumerConfigData.getMaxPollIntervalMs()).thenReturn(300000);
        when(kafkaConsumerConfigData.getMaxPartitionFetchBytesDefault()).thenReturn(1048576);
        when(kafkaConsumerConfigData.getMaxPartitionFetchBytesBoostFactor()).thenReturn(2);
        when(kafkaConsumerConfigData.getMaxPollRecords()).thenReturn(500);
    }

    @Test
    void shouldReturnConsumerConfigs() {
        Map<String, Object> consumerConfigs = kafkaConsumerConfig.consumerConfigs();

        assertEquals("localhost:9092", consumerConfigs.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", consumerConfigs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals("io.confluent.kafka.serializers.KafkaAvroDeserializer", consumerConfigs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        assertEquals("earliest", consumerConfigs.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
        assertEquals("http://localhost:8081", consumerConfigs.get("schema.registry.url"));
        assertEquals(15000, consumerConfigs.get(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG));
        assertEquals(3000, consumerConfigs.get(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG));
        assertEquals(300000, consumerConfigs.get(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG));
        assertEquals(2097152, consumerConfigs.get(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG));
        assertEquals(500, consumerConfigs.get(ConsumerConfig.MAX_POLL_RECORDS_CONFIG));
    }

    @Test
    void shouldReturnConsumerFactory() {
        ConsumerFactory<Serializable, SpecificRecordBase> consumerFactory = kafkaConsumerConfig.consumerFactory();

        assertEquals(DefaultKafkaConsumerFactory.class, consumerFactory.getClass());
    }

    @Test
    void shouldReturnKafkaListenerContainerFactory() {
        KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Serializable, SpecificRecordBase>> factory = kafkaConsumerConfig.kafkaListenerContainerFactory();

        assertEquals(ConcurrentKafkaListenerContainerFactory.class, factory.getClass());
    }
}
