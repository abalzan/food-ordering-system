package com.andrei.food.ordering.service.domain.mapper;

import com.andrei.food.ordering.service.domain.dto.create.CreateOrderCommand;
import com.andrei.food.ordering.service.domain.dto.create.CreateOrderResponse;
import com.andrei.food.ordering.service.domain.dto.create.OrderAddress;
import com.andrei.food.ordering.service.domain.dto.create.OrderItem;
import com.andrei.food.ordering.service.domain.dto.track.TrackOrderResponse;
import com.andrei.food.ordering.system.domain.entity.Order;
import com.andrei.food.ordering.system.domain.entity.Product;
import com.andrei.food.ordering.system.domain.entity.Restaurant;
import com.andrei.food.ordering.system.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(createOrderCommand.restaurantId()))
                .products(createOrderCommand.items().stream().map(orderItem ->
                        new Product(new ProductId(orderItem.productId()))).collect(Collectors.toList()))
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.Builder.builder()
                .customerId(new CustomerId(createOrderCommand.customerId()))
                .restaurantId(new RestaurantId(createOrderCommand.restaurantId()))
                .deliveryAddress(orderAddressToDeliveryAddress(createOrderCommand.address()))
                .price(new Money(createOrderCommand.price()))
                .items(orderItemsToOrderItemsEntities(createOrderCommand.items()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

    private List<com.andrei.food.ordering.system.domain.entity.OrderItem> orderItemsToOrderItemsEntities(List<OrderItem> orderItems) {
        return orderItems.stream().map(orderItem -> com.andrei.food.ordering.system.domain.entity.OrderItem.Builder.builder()
                .product(new Product(new ProductId(orderItem.productId())))
                .quantity(orderItem.quantity())
                .price(new Money(orderItem.price()))
                .subTotal(new Money(orderItem.subTotal()))
                .build()).collect(Collectors.toList());
    }

    private StreetAddress orderAddressToDeliveryAddress(OrderAddress orderAddress) {
        return new StreetAddress(UUID.randomUUID(), orderAddress.street(), orderAddress.postalCode(), orderAddress.city());
    }


}
