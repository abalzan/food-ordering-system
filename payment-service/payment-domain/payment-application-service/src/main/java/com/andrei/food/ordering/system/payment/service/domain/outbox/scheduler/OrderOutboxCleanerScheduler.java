package com.andrei.food.ordering.system.payment.service.domain.outbox.scheduler;

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

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        orderOutboxHelper.getOrderOutboxMessagesByOutboxStatus(OutboxStatus.COMPLETED)
                .ifPresent(orderOutboxMessages -> {
                    log.info("Received {} outbox messages with ids {}, deleting them",
                            orderOutboxMessages.size(),
                            orderOutboxMessages.stream().map(orderOutboxMessage -> orderOutboxMessage.getId().toString()).toList());

                    orderOutboxHelper.deleteOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);

                    log.info("{} Outbox messages deleted successfully", orderOutboxMessages.size());

                });


    }
}
