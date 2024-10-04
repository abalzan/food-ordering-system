package com.andrei.food.ordering.system.payment.service.messaging.publisher.kafka;

import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.andrei.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import com.andrei.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import com.andrei.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import com.andrei.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventKafkaPublisher implements PaymentResponseMessagePublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage, BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
        OrderEventPayload orderEventPayload = kafkaMessageHelper.getOrderEventPayload(orderOutboxMessage.getPayload(), OrderEventPayload.class);

        String sagaId = orderOutboxMessage.getSagaId().toString();

        log.info("Received OrderOutboxMessage for order with id {} and saga id {}", orderEventPayload.getOrderId(), sagaId);

        try {
            PaymentResponseAvroModel paymentResponseAvroModel = paymentMessagingDataMapper.orderEventPayloadToPaymentResponseAvroModel(sagaId, orderEventPayload);
            kafkaProducer.send(paymentServiceConfigData.getPaymentResponseTopicName(), sagaId, paymentResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallback(
                            paymentServiceConfigData.getPaymentResponseTopicName(),
                            paymentResponseAvroModel,
                            orderOutboxMessage, outboxCallback, orderEventPayload.getOrderId(), "PaymentResponseAvroModel"));

            log.info("Sent PaymentResponseAvroModel for order with id {} and saga id {}", orderEventPayload.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error occurred while sending PaymentResponseAvroModel for order with id {} and saga id {}. Error: {}", orderEventPayload.getOrderId(), sagaId, e.getMessage());
            outboxCallback.accept(orderOutboxMessage, OutboxStatus.FAILED);
        }

    }
}
