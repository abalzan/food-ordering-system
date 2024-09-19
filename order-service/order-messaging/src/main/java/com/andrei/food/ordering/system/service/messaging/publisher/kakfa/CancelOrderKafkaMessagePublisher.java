package com.andrei.food.ordering.system.service.messaging.publisher.kakfa;

import com.andrei.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.andrei.food.ordering.system.service.domain.config.OrderServiceConfigData;
import com.andrei.food.ordering.system.service.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CancelOrderKafkaMessagePublisher implements DomainEventPublisher<OrderCancelledEvent> {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(OrderCancelledEvent domainEvent) {
        try {
            String orderId = domainEvent.getOrder().getId().getValue().toString();
            log.info("Received order cancelled event for order id: {}", orderId);
            PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper.orderCancelledEventToPaymentRequestAvroModel(domainEvent);
            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentRequestAvroModel,
                    (result, ex) -> {
                        if (ex != null) {
                            // Handle failure
                            log.error("Error sending Kafka message", ex);
                        } else {
                            // Handle success
                            RecordMetadata metadata = result.getRecordMetadata();
                            log.info("Successfully sent message to Kafka. Topic: {}, Partition: {}, Offset: {}, Timestamp: {}",
                                    metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());
                        }
                    }
            );
            log.info("PaymentRequestAvroModel sent to kafka for order id: {}", paymentRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending message to kafka topic: {} with message: {}", orderServiceConfigData.getPaymentRequestTopicName(), domainEvent, e);
        }
    }
}
