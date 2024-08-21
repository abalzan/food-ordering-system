package com.andrei.food.ordering.system.payment.service.messaging.listener.kafka;

import com.andrei.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentOrderStatus;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.andrei.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import com.andrei.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    @KafkaListener(id="${kafka-consumer-config.payment-consumer-group-id}",
            topics = "${payment-service.payment-request-topic-name}")
    @Override
    public void receive(@Payload List<PaymentRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment requests received with Keys {}, Partitions {}, Offsets {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(paymentRequestAvroModel -> {
            if(paymentRequestAvroModel.getPaymentOrderStatus().equals(PaymentOrderStatus.PENDING)) {
                log.info("Processing payment request for orderId: {}", paymentRequestAvroModel.getOrderId());
                paymentRequestMessageListener.completePayment(paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel));
            } else if(paymentRequestAvroModel.getPaymentOrderStatus().equals(PaymentOrderStatus.CANCELLED)) {
                log.info("Processing payment cancellation request for orderId: {}", paymentRequestAvroModel.getOrderId());
                paymentRequestMessageListener.cancelPayment(paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel));
            }
        });
    }
}
