package com.andrei.food.ordering.system.restaurant.service.domain.outbox.scheduler;

import com.andrei.food.ordering.system.outbox.OutboxScheduler;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxCleanerScheduler implements OutboxScheduler {

    private final OrderOutboxHelper orderOutboxHelper;

    @Transactional
    @Scheduled(cron = "@midnight")
    @Override
    public void processOutboxMessage() {
        orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED)
            .ifPresent(outboxMessages -> {
                log.info("Received {} OrderOutboxMessage for clean-up!", outboxMessages.size());
                orderOutboxHelper.deleteOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);
                log.info("Deleted {} OrderOutboxMessage!", outboxMessages.size());
            });
    }
}