package com.andrei.food.ordering.system.service.domain.outbox.scheduler.approval;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.order.SagaConstants;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.andrei.food.ordering.system.service.domain.ports.output.repository.ApprovalOutboxRepository;
import com.andrei.food.ordering.system.service.exception.OrderDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApprovalOutboxHelper {

    private final ApprovalOutboxRepository approvalOutboxRepository;

    @Transactional(readOnly = true)
    public Optional<List<OrderApprovalOutboxMessage>> getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus outboxStatus, SagaStatus... sagaStatus) {

        return approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(SagaConstants.ORDER_SAGA_NAME, outboxStatus, sagaStatus);
    }

    @Transactional(readOnly = true)
    public Optional<OrderApprovalOutboxMessage> getApprovalOutboxMessageBySagaIdAndSagaStatus(
            UUID sagaId, SagaStatus... sagaStatus) {

        return approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(SagaConstants.ORDER_SAGA_NAME, sagaId, sagaStatus);
    }

    @Transactional
    public void save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        OrderApprovalOutboxMessage response = approvalOutboxRepository.save(orderApprovalOutboxMessage);
        if(response == null) {
            log.error("Failed to save OrderApprovalOutboxMessage with id {}", orderApprovalOutboxMessage.getId());
            throw new OrderDomainException("Failed to save OrderApprovalOutboxMessage with id " + orderApprovalOutboxMessage.getId());
        }
        log.info("OrderApprovalOutboxMessage with id {} was saved", orderApprovalOutboxMessage.getId());
    }

    @Transactional
    public void deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(SagaConstants.ORDER_SAGA_NAME, outboxStatus, sagaStatus);
        log.info("OrderApprovalOutboxMessage with outbox status {} and saga status {} was deleted", outboxStatus, sagaStatus);
    }
}
