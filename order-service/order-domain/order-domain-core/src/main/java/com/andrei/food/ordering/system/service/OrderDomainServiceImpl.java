package com.andrei.food.ordering.system.service;

import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;
import com.andrei.food.ordering.system.service.valueobject.ProductId;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.entity.Product;
import com.andrei.food.ordering.system.service.entity.Restaurant;
import com.andrei.food.ordering.system.service.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.service.event.OrderCreatedEvent;
import com.andrei.food.ordering.system.service.event.OrderPaidEvent;
import com.andrei.food.ordering.system.service.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.andrei.food.ordering.system.service.DomainConstants.UTC;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService{

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant, DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order {} has been created", order.getId().getValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)), orderCreatedEventDomainEventPublisher);
    }

    @Override
    public OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher) {
        order.pay();
        log.info("Order {} has been paid", order.getId().getValue());
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC)), orderPaidEventDomainEventPublisher);
    }

    @Override
    public void ApproveOrder(Order order) {
        order.approve();
        log.info("Order {} has been approved", order.getId().getValue());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages, DomainEventPublisher<OrderCancelledEvent> orderCancelledEventDomainEventPublisher) {
        order.initCancel(failureMessages);
        log.info("Order payment is cancelling for orderId {}", order.getId().getValue());
        return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)), orderCancelledEventDomainEventPublisher);
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order {} has been cancelled", order.getId().getValue());

    }

    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        Map<ProductId, Product> restaurantProductMap = new HashMap<>();
        restaurant.getProducts().forEach(restaurantProduct ->{
            restaurantProductMap.put(restaurantProduct.getId(), restaurantProduct);
        });
        order.getItems().forEach(orderItem ->  {
            Product currentProduct = orderItem.getProduct();
            Product restaurantProduct = restaurantProductMap.get(currentProduct.getId());
            currentProduct.updateWithConfirmedNameAndPrice(restaurantProduct.getName(),
                    restaurantProduct.getPrice());
        });
    }

    private void validateRestaurant(Restaurant restaurant) {
        if(!restaurant.isActive()){
            throw new OrderDomainException("Restaurant with id " + restaurant.getId().getValue() + " is currently not active");
        }

    }
}
