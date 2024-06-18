package com.andrei.food.ordering.system.kafka.producer;

import com.andrei.food.ordering.system.kafka.config.data.KafkaConfigData;
import com.andrei.food.ordering.system.kafka.config.data.KafkaProducerConfigData;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.Serializable;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class KafkaProducerConfigTest {

    @Mock
    private KafkaConfigData kafkaConfigData;

    @Mock
    private KafkaProducerConfigData kafkaProducerConfigData;

    private KafkaProducerConfig<Serializable, SpecificRecordBase> kafkaProducerConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaProducerConfig = new KafkaProducerConfig<>(kafkaConfigData, kafkaProducerConfigData);
        when(kafkaConfigData.getBootstrapServers()).thenReturn("localhost:9092");
        when(kafkaConfigData.getSchemaRegistryUrlKey()).thenReturn("schema.registry.url");
        when(kafkaConfigData.getSchemaRegistryUrl()).thenReturn("http://localhost:8081");
        when(kafkaProducerConfigData.getKeySerializerClass()).thenReturn("org.apache.kafka.common.serialization.StringSerializer");
        when(kafkaProducerConfigData.getValueSerializerClass()).thenReturn("io.confluent.kafka.serializers.KafkaAvroSerializer");
        when(kafkaProducerConfigData.getBatchSize()).thenReturn(16384);
        when(kafkaProducerConfigData.getBatchSizeBoostFactor()).thenReturn(2);
        when(kafkaProducerConfigData.getLingerMs()).thenReturn(1);
        when(kafkaProducerConfigData.getCompressionType()).thenReturn("snappy");
        when(kafkaProducerConfigData.getAcks()).thenReturn("all");
        when(kafkaProducerConfigData.getRequestTimeoutMs()).thenReturn(30000);
        when(kafkaProducerConfigData.getRetryCount()).thenReturn(3);
    }

    @Test
    void shouldReturnProducerConfig() {
        Map<String, Object> producerConfig = kafkaProducerConfig.producerConfig();

        assertEquals("localhost:9092", producerConfig.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("http://localhost:8081", producerConfig.get("schema.registry.url"));
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", producerConfig.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals("io.confluent.kafka.serializers.KafkaAvroSerializer", producerConfig.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
        assertEquals(32768, producerConfig.get(ProducerConfig.BATCH_SIZE_CONFIG));
        assertEquals(1, producerConfig.get(ProducerConfig.LINGER_MS_CONFIG));
        assertEquals("snappy", producerConfig.get(ProducerConfig.COMPRESSION_TYPE_CONFIG));
        assertEquals("all", producerConfig.get(ProducerConfig.ACKS_CONFIG));
        assertEquals(30000, producerConfig.get(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG));
        assertEquals(3, producerConfig.get(ProducerConfig.RETRIES_CONFIG));
    }

    @Test
    void shouldReturnProducerFactory() {
        ProducerFactory<Serializable, SpecificRecordBase> producerFactory = kafkaProducerConfig.producerFactory();

        assertEquals(DefaultKafkaProducerFactory.class, producerFactory.getClass());
    }

    @Test
    void shouldReturnKafkaTemplate() {
        KafkaTemplate<Serializable, SpecificRecordBase> kafkaTemplate = kafkaProducerConfig.kafkaTemplate();

        assertEquals(KafkaTemplate.class, kafkaTemplate.getClass());
    }
}
