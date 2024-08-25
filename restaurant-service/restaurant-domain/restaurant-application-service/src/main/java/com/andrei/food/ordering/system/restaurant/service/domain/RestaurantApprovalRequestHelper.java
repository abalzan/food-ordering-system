package com.andrei.food.ordering.system.restaurant.service.domain;

import com.andrei.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.andrei.food.ordering.system.restaurant.service.domain.mapper.RestaurantDataMapper;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
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
    private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
    private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    @Transactional
    public OrderApprovalEvent persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        log.info("Processing restaurant approval for order {}", restaurantApprovalRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Restaurant restaurant = findRestaurant(restaurantApprovalRequest);
        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages, orderApprovedMessagePublisher, orderRejectedMessagePublisher);

        orderApprovalRepository.save(orderApprovalEvent.getOrderApproval());

        return orderApprovalEvent;
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
}
