package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.SagaStep;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.DomainConstants;
import com.andrei.food.ordering.system.service.OrderDomainService;
import com.andrei.food.ordering.system.service.domain.dto.message.RestaurantApprovalResponse;
import com.andrei.food.ordering.system.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.andrei.food.ordering.system.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.andrei.food.ordering.system.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.service.exception.OrderDomainException;
import com.andrei.food.ordering.system.service.valueobject.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {

    private final OrderDomainService   orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderDataMapper orderDataMapper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;

    @Override
    @Transactional
    public void process(RestaurantApprovalResponse restaurantApprovalResponse) {

        approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(restaurantApprovalResponse.getSagaId()), SagaStatus.PROCESSING)
                .ifPresentOrElse(orderApprovalOutboxMessage -> {
                   Order order = approveOrder(restaurantApprovalResponse);

                    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());
                    approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage,
                            order.getOrderStatus(), sagaStatus));

                    paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(restaurantApprovalResponse.getSagaId(),
                            order.getOrderStatus(), sagaStatus));

                    log.info("Approval for order with id {} was approved", restaurantApprovalResponse.getOrderId());

                }, () -> log.info("Approval Outbox Message is already processed for saga id {}", restaurantApprovalResponse.getSagaId()));
    }

    @Override
    @Transactional
    public void rollback(RestaurantApprovalResponse restaurantApprovalResponse) {

        approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(restaurantApprovalResponse.getSagaId()), SagaStatus.PROCESSING)
                .ifPresentOrElse(orderApprovalOutboxMessage -> {
                    OrderCancelledEvent orderCancelledEvent = rollbackOrder(restaurantApprovalResponse);

                    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderCancelledEvent.getOrder().getOrderStatus());

                    approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage,
                            orderCancelledEvent.getOrder().getOrderStatus(), sagaStatus));

                    paymentOutboxHelper.savePaymentOutboxMessage(orderDataMapper.orderCancelledEventToOrderPaymentEventPayload(orderCancelledEvent),
                            orderCancelledEvent.getOrder().getOrderStatus(),
                            sagaStatus,
                            OutboxStatus.STARTED,
                            UUID.fromString(restaurantApprovalResponse.getSagaId()
                    ));
                    log.info("Order with id {} is cancelling", restaurantApprovalResponse.getOrderId());
                }, () -> log.info("Approval Outbox Message is already processed for saga id {}", restaurantApprovalResponse.getSagaId()));
    }

    private Order approveOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Completing Approval for order with id {}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.ApproveOrder(order);
        orderSagaHelper.saveOrder(order);
        return order;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                                                                       OrderStatus orderStatus,
                                                                       SagaStatus sagaStatus) {
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)));
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        return orderApprovalOutboxMessage;
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        return paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(sagaId), SagaStatus.PROCESSING)
                .map(orderPaymentOutboxMessage -> {
                    orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)));
                    orderPaymentOutboxMessage.setOrderStatus(orderStatus);
                    orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
                    return orderPaymentOutboxMessage;
                }).orElseThrow(() -> new OrderDomainException("Payment Outbox Message is not found for saga id " + sagaId + " and " + SagaStatus.PROCESSING.name() + " state"));
    }

    private OrderCancelledEvent rollbackOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Cancelling Approval for order with id {} ", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order, restaurantApprovalResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return orderCancelledEvent;
    }
}
