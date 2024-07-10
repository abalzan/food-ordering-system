package com.andrei.food.ordering.system.service.messaging.listener.kafka;

import com.andrei.food.ordering.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.andrei.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public PaymentResponseKafkaListener(PaymentResponseMessageListener paymentResponseMessageListener, OrderMessagingDataMapper orderMessagingDataMapper) {
        this.paymentResponseMessageListener = paymentResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${order-service.payment-response-topic-name}")
    public void receive(@Payload List<PaymentResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment responses received with keys {} from partitions {} with offsets {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(paymentResponseAvroModel -> {
            if(PaymentStatus.COMPLETED.equals(paymentResponseAvroModel.getPaymentStatus())){
                log.info("Payment response with id {} is completed for order id {}", paymentResponseAvroModel.getPaymentId(), paymentResponseAvroModel.getOrderId());
                paymentResponseMessageListener.paymentCompleted(orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
            } else if(PaymentStatus.FAILED.equals(paymentResponseAvroModel.getPaymentStatus()) || PaymentStatus.CANCELLED.equals(paymentResponseAvroModel.getPaymentStatus())){
                log.info("Payment response with id {} is failed for order id {}", paymentResponseAvroModel.getPaymentId(), paymentResponseAvroModel.getOrderId());
                paymentResponseMessageListener.paymentCancelled(orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
            }
        });


    }
}