package com.andrei.food.ordering.system.payment.service.domain.outbox.scheduler;

import com.andrei.food.ordering.system.outbox.OutboxScheduler;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxScheduler implements OutboxScheduler {

    private final OrderOutboxHelper orderOutboxHelper;
    private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;


    @Override
    @Transactional
    @Scheduled(fixedRateString = "${payment-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${payment-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        orderOutboxHelper.getOrderOutboxMessagesByOutboxStatus(OutboxStatus.STARTED)
                .ifPresent(orderOutboxMessages -> {
                    log.info("Received {} outbox messages with ids {}, sending them to the outbox publisher",
                            orderOutboxMessages.size(),
                            orderOutboxMessages.stream().map(orderOutboxMessage -> orderOutboxMessage.getId().toString()).toList());

                    orderOutboxMessages.forEach(orderOutboxMessage ->
                            paymentResponseMessagePublisher.publish(orderOutboxMessage, orderOutboxHelper::updateOutboxMessage));

                    log.info("{} Outbox messages sent to the outbox publisher successfully", orderOutboxMessages.size());

                });
    }
}
