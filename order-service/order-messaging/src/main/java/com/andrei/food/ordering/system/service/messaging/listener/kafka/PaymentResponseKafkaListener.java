package com.andrei.food.ordering.system.service.messaging.listener.kafka;

import com.andrei.food.ordering.system.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.andrei.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.andrei.food.ordering.system.service.exception.OrderNotFoundException;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${order-service.payment-response-topic-name}")
    public void receive(@Payload List<PaymentResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment responses received with keys {} from partitions {} with offsets {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(paymentResponseAvroModel -> {
            try {
                if(PaymentStatus.COMPLETED.equals(paymentResponseAvroModel.getPaymentStatus())){
                    log.info("Payment response with id {} is completed for order id {}", paymentResponseAvroModel.getPaymentId(), paymentResponseAvroModel.getOrderId());
                    paymentResponseMessageListener.paymentCompleted(orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
                } else if(PaymentStatus.FAILED.equals(paymentResponseAvroModel.getPaymentStatus()) || PaymentStatus.CANCELLED.equals(paymentResponseAvroModel.getPaymentStatus())){
                    log.info("Payment response with id {} is failed for order id {}", paymentResponseAvroModel.getPaymentId(), paymentResponseAvroModel.getOrderId());
                    paymentResponseMessageListener.paymentCancelled(orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
                }
            } catch (OptimisticLockingFailureException e) {
                // This means another thread has updated the order status, so we can ignore this exception
                log.error("OptimisticLockingFailureException occurred while processing payment response with id {} for order id {}", paymentResponseAvroModel.getPaymentId(), paymentResponseAvroModel.getOrderId(), e);
            } catch (OrderNotFoundException e) {
                log.error("OrderNotFoundException occurred while processing payment response with id {} for order id {}", paymentResponseAvroModel.getPaymentId(), paymentResponseAvroModel.getOrderId(), e);
            }
        });


    }
}
