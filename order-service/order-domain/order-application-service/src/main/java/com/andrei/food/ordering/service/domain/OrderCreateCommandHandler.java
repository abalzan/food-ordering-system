package com.andrei.food.ordering.service.domain;

import com.andrei.food.ordering.service.domain.dto.create.CreateOrderCommand;
import com.andrei.food.ordering.service.domain.dto.create.CreateOrderResponse;
import com.andrei.food.ordering.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.andrei.food.ordering.service.domain.ports.output.repository.CustomerRepository;
import com.andrei.food.ordering.service.domain.ports.output.repository.OrderRepository;
import com.andrei.food.ordering.service.domain.ports.output.repository.RestaurantRepository;
import com.andrei.food.ordering.system.domain.OrderDomainService;
import com.andrei.food.ordering.system.domain.entity.Order;
import com.andrei.food.ordering.system.domain.entity.Restaurant;
import com.andrei.food.ordering.system.domain.event.OrderCreatedEvent;
import com.andrei.food.ordering.system.domain.exception.OrderDomainException;
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
    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand){
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info("Order with id {} created", orderCreatedEvent.getOrder().getId().getValue());
        orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);
        return orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder());
    }
}