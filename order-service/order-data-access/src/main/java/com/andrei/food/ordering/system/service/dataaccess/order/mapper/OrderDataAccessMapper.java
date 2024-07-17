package com.andrei.food.ordering.system.service.dataaccess.order.mapper;

import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.entity.OrderItem;
import com.andrei.food.ordering.system.service.entity.Product;
import com.andrei.food.ordering.system.service.valueobject.*;
import com.andrei.food.ordering.system.service.dataaccess.order.entity.OrderAddressEntity;
import com.andrei.food.ordering.system.service.dataaccess.order.entity.OrderEntity;
import com.andrei.food.ordering.system.service.dataaccess.order.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.andrei.food.ordering.system.service.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Component
public class OrderDataAccessMapper {

    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .trackingId(order.getTrackingId().getValue())
                .address(deliverAddressToAddressEntity(order.getDeliveryAddress()))
                .price(order.getPrice().getAmount())
                .items(orderItemsToOrderItemsEntities(order.getItems()))
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages() != null ? String.join(FAILURE_MESSAGE_DELIMITER, order.getFailureMessages()) : "")
                .build();
        orderEntity.getAddress().setOrder(orderEntity);
        orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));
        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.Builder.builder()
                .orderId(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
                .price(new Money(orderEntity.getPrice()))
                .items(orderItemsEntitiesToOrderItems(orderEntity.getItems()))
                .trackingId(new TrackingId(orderEntity.getTrackingId()))
                .orderStatus(orderEntity.getOrderStatus())
                .failureMessages(orderEntity.getFailureMessages().isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER))))
                .build();
    }

    private List<OrderItem> orderItemsEntitiesToOrderItems(List<OrderItemEntity> items) {
        return items.stream()
                .map(orderItemEntity ->OrderItem.Builder.builder()
                        .orderItemId(new OrderItemId(orderItemEntity.getId()))
                        .product(new Product(new ProductId(orderItemEntity.getProductId())))
                        .quantity(orderItemEntity.getQuantity())
                        .price(new Money(orderItemEntity.getPrice()))
                        .subTotal(new Money(orderItemEntity.getSubTotal()))
                        .build()).toList();
    }

    private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity address) {
        return new StreetAddress(address.getId(), address.getStreet(), address.getCity(), address.getPostalCode());
    }

    private List<OrderItemEntity> orderItemsToOrderItemsEntities(List<OrderItem> items) {
        return items.stream().map(item -> OrderItemEntity.builder()
                .id(item.getId().getValue())
                .productId(item.getProduct().getId().getValue())
                .quantity(item.getQuantity())
                .price(item.getPrice().getAmount())
                .subTotal(item.getSubTotal().getAmount())
                .build()).toList();
    }

    private OrderAddressEntity deliverAddressToAddressEntity(StreetAddress deliveryAddress) {
        return OrderAddressEntity.builder()
                .id(deliveryAddress.getId())
                .street(deliveryAddress.getStreet())
                .city(deliveryAddress.getCity())
                .postalCode(deliveryAddress.getPostalCode())
                .build();
    }
}
