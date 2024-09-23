package com.andrei.food.ordering.system.service.domain.outbox.scheduler.approval;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxScheduler;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.andrei.food.ordering.system.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
public class RestaurantApprovalOutboxScheduler implements OutboxScheduler {

    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher;

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        Optional<List<OrderApprovalOutboxMessage>> outboxMessagesResponse = approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.STARTED,
                SagaStatus.PROCESSING);

        if(outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            List<OrderApprovalOutboxMessage> outboxMessages = outboxMessagesResponse.get();
            log.info("Received {} OrderApprovalOutboxMessage with ids {}", outboxMessages.size(),
                    outboxMessages.stream().map(outboxMessage -> outboxMessage.getId().toString()).collect(Collectors.joining(", ")));

            outboxMessages.forEach(outboxMessage -> {
                log.info("Processing OrderApprovalOutboxMessage with id {}", outboxMessage.getId());
                restaurantApprovalRequestMessagePublisher.publish(outboxMessage, this::updateOutboxStatus);
            });
            log.info("Processed {} OrderApprovalOutboxMessage sent to message bus", outboxMessages.size());
        }
    }

    private void updateOutboxStatus(OrderApprovalOutboxMessage orderApprovalOutboxMessage, OutboxStatus outboxStatus) {
        orderApprovalOutboxMessage.setOutboxStatus(outboxStatus);
        approvalOutboxHelper.save(orderApprovalOutboxMessage);
        log.info("OrderApprovalOutboxMessage with id {} was updated to status {}", orderApprovalOutboxMessage.getId(), outboxStatus);
    }
}
