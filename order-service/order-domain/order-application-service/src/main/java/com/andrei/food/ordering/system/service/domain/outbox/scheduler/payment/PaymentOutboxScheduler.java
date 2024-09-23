package com.andrei.food.ordering.system.service.domain.outbox.scheduler.payment;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxScheduler;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.andrei.food.ordering.system.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxScheduler implements OutboxScheduler {

    private final PaymentOutboxHelper paymentOutboxHelper;
    private final PaymentRequestMessagePublisher paymentRequestMessagePublisher;

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        Optional<List<OrderPaymentOutboxMessage>> outboxMessagesResponse = paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus.STARTED,
                SagaStatus.STARTED, SagaStatus.COMPENSATING);

        if(outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            List<OrderPaymentOutboxMessage> outboxMessages = outboxMessagesResponse.get();
            log.info("Received {} OrderPaymentOutboxMessage with ids {}", outboxMessages.size(),
                    outboxMessages.stream().map(outboxMessage -> outboxMessage.getId().toString()).collect(Collectors.joining(", ")));

            outboxMessages.forEach(outboxMessage -> {
                log.info("Processing OrderPaymentOutboxMessage with id {}", outboxMessage.getId());
                paymentRequestMessagePublisher.publish(outboxMessage, this::updateOutboxStatus);

            });
        }
    }

    private void updateOutboxStatus(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OutboxStatus outboxStatus) {
        orderPaymentOutboxMessage.setOutboxStatus(outboxStatus);
        paymentOutboxHelper.save(orderPaymentOutboxMessage);
        log.info("OrderPaymentOutboxMessage with id {} was updated to status {}", orderPaymentOutboxMessage.getId(), outboxStatus);
    }
}
