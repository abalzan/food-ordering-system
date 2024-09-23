package com.andrei.food.ordering.system.service.domain.ports.output.repository;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentOutboxRepository {

    OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage orderPaymentOutboxMessage);

    Optional<List<OrderPaymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);

    Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String type, UUID sagaId, SagaStatus... sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatus(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);

}
