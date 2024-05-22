package com.andrei.food.ordering.service.domain.ports.output.message.publisher.restaurantapproval;

import com.andrei.food.ordering.system.domain.event.OrderPaidEvent;
import com.andrei.food.ordering.system.domain.events.publisher.DomainEventPublisher;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
