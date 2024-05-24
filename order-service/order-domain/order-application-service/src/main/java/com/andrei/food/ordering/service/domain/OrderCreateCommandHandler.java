package com.andrei.food.ordering.service.domain;

import com.andrei.food.ordering.service.domain.dto.create.CreateOrderCommand;
import com.andrei.food.ordering.service.domain.dto.create.CreateOrderResponse;
import com.andrei.food.ordering.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.service.domain.ports.output.repository.CustomerRepository;
import com.andrei.food.ordering.service.domain.ports.output.repository.OrderRepository;
import com.andrei.food.ordering.service.domain.ports.output.repository.RestaurantRepository;
import com.andrei.food.ordering.system.domain.OrderDomainService;
import com.andrei.food.ordering.system.domain.entity.Customer;
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
    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand){
        checkCustomer(createOrderCommand.customerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
        Order orderResult = saveOrder(order);
        log.info("Order with id {} created", orderResult.getId().getValue());
        return orderDataMapper.orderToCreateOrderResponse(orderResult);
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);

        return restaurantRepository.findRestaurantInformation(restaurant)
            .orElseThrow(() -> {
                log.warn("Restaurant with id {} not found", createOrderCommand.restaurantId());
                return new OrderDomainException("Could not find restaurant with id: " + createOrderCommand.restaurantId());
            });
    }

    private void checkCustomer(UUID customerId) {
        customerRepository.findCustomer(customerId)
            .orElseThrow(() -> {
                log.warn("Customer with id {} not found", customerId);
                return new OrderDomainException("Could not find customer with id: " + customerId);
            });
    }

    private Order saveOrder(Order order) {
        Order savedOrder = orderRepository.saveOrder(order);
        if (savedOrder == null) {
            log.warn("Order with id {} not saved", order.getId());
            throw new OrderDomainException("Could not save order with id: " + order.getId());
        }
        log.info("Order with id {} saved", savedOrder.getId());
        return savedOrder;
    }
}
