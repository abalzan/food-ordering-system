package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.SagaStep;
import com.andrei.food.ordering.system.service.OrderDomainService;
import com.andrei.food.ordering.system.service.domain.dto.message.PaymentResponse;
import com.andrei.food.ordering.system.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.andrei.food.ordering.system.service.domain.ports.output.repository.OrderRepository;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.event.OrderPaidEvent;
import com.andrei.food.ordering.system.service.events.EmptyEvent;
import com.andrei.food.ordering.system.service.exception.OrderNotFoundException;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

    @Override
    @Transactional
    public OrderPaidEvent process(PaymentResponse paymentResponse) {
        log.info("Completing Payment for order with id {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher);
        orderSagaHelper.saveOrder(order);
        log.info("Payment for order with id {} was successful", order.getId().getValue());
        return orderPaidEvent;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(PaymentResponse paymentResponse) {
        log.info("Cancelling Payment for order with id {} ", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        log.info("Payment for order with id {} was cancelled", order.getId().getValue());
        return EmptyEvent.INSTANCE;
    }
}
