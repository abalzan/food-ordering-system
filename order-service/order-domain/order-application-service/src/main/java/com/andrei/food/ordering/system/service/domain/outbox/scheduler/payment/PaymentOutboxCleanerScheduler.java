package com.andrei.food.ordering.system.service.domain.outbox.scheduler.payment;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxScheduler;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
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
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {

    private final PaymentOutboxHelper paymentOutboxHelper;

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        Optional<List<OrderPaymentOutboxMessage>> outboxMessagesResponse =
                paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                        OutboxStatus.COMPLETED,
                        SagaStatus.COMPENSATED,
                        SagaStatus.SUCCEEDED,
                        SagaStatus.FAILED);

        if (outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            List<OrderPaymentOutboxMessage> outboxMessages = outboxMessagesResponse.get();
            log.info("Received {} OrderPaymentOutboxMessage with ids {}", outboxMessages.size(),
                    outboxMessages.stream().map(OrderPaymentOutboxMessage::getPayload).collect(Collectors.joining("\n")));

            outboxMessages.forEach(outboxMessage -> {
                log.info("Processing OrderPaymentOutboxMessage with id {}", outboxMessage.getId());
                paymentOutboxHelper.deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
                        OutboxStatus.COMPLETED,
                        SagaStatus.COMPENSATED,
                        SagaStatus.SUCCEEDED,
                        SagaStatus.FAILED);

                //TODO: save the outbox message in an archive table for audit purposes

                log.info("OrderPaymentOutboxMessage with id {} was deleted", outboxMessage.getId());
            });
        }
    }
}
