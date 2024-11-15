package com.andrei.food.ordering.system.payment.service.messaging.listener.kafka;

import com.andrei.food.ordering.system.domain.exception.PaymentNotFoundException;
import com.andrei.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentOrderStatus;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.andrei.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.andrei.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import com.andrei.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    @KafkaListener(groupId="${kafka-consumer-config.payment-consumer-group-id}",
            id = "${kafka-consumer-config.payment-consumer-group-id}",
            topics = "${payment-service.payment-request-topic-name}")
    @Override
    public void receive(@Payload List<PaymentRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment requests received with Keys {}, Partitions {}, Offsets {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(paymentRequestAvroModel -> {
            try {
                if(paymentRequestAvroModel.getPaymentOrderStatus().equals(PaymentOrderStatus.PENDING)) {
                    log.info("Processing payment request for orderId: {}", paymentRequestAvroModel.getOrderId());
                    paymentRequestMessageListener.completePayment(paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel));
                } else if(paymentRequestAvroModel.getPaymentOrderStatus().equals(PaymentOrderStatus.CANCELLED)) {
                    log.info("Processing payment cancellation request for orderId: {}", paymentRequestAvroModel.getOrderId());
                    paymentRequestMessageListener.cancelPayment(paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel));
                }
            } catch (DataAccessException e) {
                SQLException sqlException = (SQLException) e.getRootCause();
                if (sqlException != null && sqlException.getSQLState() != null &&
                        PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                    // This is a unique violation exception, which means that the payment request has already been processed
                    log.error("Caught unique violation exception with sql state: {} while processing payment request for orderId: {}", sqlException.getSQLState(), paymentRequestAvroModel.getOrderId());
                } else {
                    throw new PaymentApplicationServiceException("Throwing DataAccessException while processing payment request for orderId: " + paymentRequestAvroModel.getOrderId(), e);
                }
            } catch (PaymentNotFoundException e) {
                log.error("Caught PaymentNotFoundException while processing payment request for orderId: {}", paymentRequestAvroModel.getOrderId());
            }
        });
    }
}
