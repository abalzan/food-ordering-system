package com.andrei.food.ordering.system.service.domain.outbox.scheduler.approval;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxScheduler;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class RestaurantApprovalOutboxCleanerScheduler implements OutboxScheduler {

    private final ApprovalOutboxHelper approvalOutboxHelper;

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        Optional<List<OrderApprovalOutboxMessage>> outboxMessagesResponse = approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED,
                SagaStatus.COMPENSATED,
                SagaStatus.SUCCEEDED,
                SagaStatus.FAILED);

        if (outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            List<OrderApprovalOutboxMessage> outboxMessages = outboxMessagesResponse.get();
            log.info("Received {} OrderApprovalOutboxMessage with ids {}", outboxMessages.size(),
                    outboxMessages.stream().map(OrderApprovalOutboxMessage::getPayload).collect(Collectors.joining("\n")));

            outboxMessages.forEach(outboxMessage -> {
                log.info("Processing OrderApprovalOutboxMessage with id {}", outboxMessage.getId());
                approvalOutboxHelper.deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                        OutboxStatus.COMPLETED,
                        SagaStatus.COMPENSATED,
                        SagaStatus.SUCCEEDED,
                        SagaStatus.FAILED);

                log.info("OrderApprovalOutboxMessage with id {} was deleted", outboxMessage.getId());
            });
        }
    }
}
