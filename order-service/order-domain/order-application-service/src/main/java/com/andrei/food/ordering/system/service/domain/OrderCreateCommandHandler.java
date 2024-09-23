package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.domain.dto.create.CreateOrderCommand;
import com.andrei.food.ordering.system.service.domain.dto.create.CreateOrderResponse;
import com.andrei.food.ordering.system.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.system.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.andrei.food.ordering.system.service.event.OrderCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final OrderSagaHelper orderSagaHelper;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand){
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info("Order with id {} created", orderCreatedEvent.getOrder().getId().getValue());
        CreateOrderResponse createOrderResponse = orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder(), "Order Created successfully");

        paymentOutboxHelper.savePaymentOutboxMessage(orderDataMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent),
                orderCreatedEvent.getOrder().getOrderStatus(),
                orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                UUID.randomUUID());

        log.info("Returned CreateOrderResponse with order id {}", orderCreatedEvent.getOrder().getId());
        return createOrderResponse;
    }
}
