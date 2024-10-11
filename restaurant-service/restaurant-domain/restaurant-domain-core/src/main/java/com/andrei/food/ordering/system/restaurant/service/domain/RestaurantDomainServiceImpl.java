package com.andrei.food.ordering.system.restaurant.service.domain;

import com.andrei.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import com.andrei.food.ordering.system.service.DomainConstants;
import com.andrei.food.ordering.system.service.valueobject.OrderApprovalStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {

    @Override
    public OrderApprovalEvent validateOrder(Restaurant restaurant,
                                            List<String> failureMessages) {
        restaurant.validateOrder(failureMessages);
        log.info("Validating order with id: {}", restaurant.getOrderDetail().getId().getValue());

        if (failureMessages.isEmpty()) {
            log.info("Order approved for restaurant with id: {}", restaurant.getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return new OrderApprovedEvent(restaurant.getOrderApproval(), restaurant.getId(), failureMessages,
                    ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)));
        } else {
            log.info("Order rejected for restaurant with id: {}", restaurant.getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);
            return new OrderRejectedEvent(restaurant.getOrderApproval(), restaurant.getId(), failureMessages,
                    ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)));
        }
    }
}
