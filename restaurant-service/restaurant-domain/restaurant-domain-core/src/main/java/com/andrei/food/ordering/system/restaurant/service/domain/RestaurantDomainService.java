package com.andrei.food.ordering.system.restaurant.service.domain;

import com.andrei.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;

import java.util.List;

public interface RestaurantDomainService {

    OrderApprovalEvent validateOrder(Restaurant restaurant,
                                           List<String> failureMessages,
                                           DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher,
                                           DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher);
}
