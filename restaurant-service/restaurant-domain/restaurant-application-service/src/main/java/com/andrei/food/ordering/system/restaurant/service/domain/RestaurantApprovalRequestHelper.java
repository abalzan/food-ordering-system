package com.andrei.food.ordering.system.restaurant.service.domain;

import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.andrei.food.ordering.system.restaurant.service.domain.mapper.RestaurantDataMapper;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.scheduler.OrderOutboxHelper;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class RestaurantApprovalRequestHelper {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderOutboxHelper orderOutboxHelper;
    private final RestaurantApprovalResponseMessagePublisher restaurantApprovalResponseMessagePublisher;

    @Transactional
    public void persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        if (publishIfOutboxMessageProcessed(restaurantApprovalRequest)) {
            log.info("An outbox message with saga id: {} already saved to database!",
                    restaurantApprovalRequest.getSagaId());
            return;
        }

        log.info("Processing restaurant approval for order {}", restaurantApprovalRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Restaurant restaurant = findRestaurant(restaurantApprovalRequest);
        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages);

        orderApprovalRepository.save(orderApprovalEvent.getOrderApproval());

        orderOutboxHelper
                .saveOrderOutboxMessage(restaurantDataMapper.orderApprovalEventToOrderEventPayload(orderApprovalEvent),
                        orderApprovalEvent.getOrderApproval().getApprovalStatus(),
                        OutboxStatus.STARTED,
                        UUID.fromString(restaurantApprovalRequest.getSagaId()));
    }

    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        Restaurant restaurant = restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
        Restaurant restaurantEntity = restaurantRepository.findRestaurantInformation(restaurant)
                .orElseThrow(() -> {
                    log.error("Restaurant with id {} not found", restaurant.getId().getValue());
                    return new RestaurantNotFoundException("Restaurant not found");
                });

        restaurant.setActive(restaurantEntity.isActive());
        restaurant.getOrderDetail().getProducts().forEach(product -> {
          restaurantEntity.getOrderDetail().getProducts().stream()
                  .filter(productEntity -> productEntity.getId().equals(product.getId()))
                  .findFirst()
                  .ifPresent(productEntity -> {
                      product.updateWithConfirmedNamePriceAndAvailability(productEntity.getName(), productEntity.getPrice(), productEntity.isAvailable());
                  });
        });

        restaurant.getOrderDetail().setId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())));

        return restaurant;
    }

    private boolean publishIfOutboxMessageProcessed(RestaurantApprovalRequest restaurantApprovalRequest) {
        return orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(
                        UUID.fromString(restaurantApprovalRequest.getSagaId()), OutboxStatus.COMPLETED)
                .map(orderOutboxMessage -> {
                    restaurantApprovalResponseMessagePublisher.publish(orderOutboxMessage, orderOutboxHelper::updateOutboxStatus);
                    log.info("Order outbox message with id {} is processed", orderOutboxMessage.getId());
                    return true;
                })
                .orElse(false);
    }
}
