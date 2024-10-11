package com.andrei.food.ordering.system.restaurant.service.domain.outbox.scheduler;

import com.andrei.food.ordering.system.outbox.OutboxScheduler;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxScheduler implements OutboxScheduler {

    private final OrderOutboxHelper orderOutboxHelper;
    private final RestaurantApprovalResponseMessagePublisher responseMessagePublisher;

    @Transactional
    @Scheduled(fixedRateString = "${restaurant-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${restaurant-service.outbox-scheduler-initial-delay}")
    @Override
    public void processOutboxMessage() {
        orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED)
                .ifPresent(outboxMessages -> {
                    log.info("Received {} OrderOutboxMessage with ids {}, sending to message bus!", outboxMessages.size(),
                            outboxMessages.stream().map(outboxMessage ->
                                    outboxMessage.getId().toString()).collect(Collectors.joining(",")));
                    outboxMessages.forEach(orderOutboxMessage ->
                            responseMessagePublisher.publish(orderOutboxMessage,
                                    orderOutboxHelper::updateOutboxStatus));
                    log.info("{} OrderOutboxMessage sent to message bus!", outboxMessages.size());
                });
    }
}