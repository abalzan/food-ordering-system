package com.andrei.food.ordering.system.service.domain.ports.output.message.publisher.restaurantapproval;

import com.andrei.food.ordering.system.service.event.OrderPaidEvent;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
