package com.andrei.food.ordering.system.restaurant.service.domain.mapper;
import com.andrei.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Product;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.andrei.food.ordering.system.service.valueobject.ProductId;
import com.andrei.food.ordering.system.service.valueobject.RestaurantOrderStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;


class RestaurantDataMapperTest {

    UUID restaurantId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();
    UUID productId = UUID.randomUUID();
    RestaurantDataMapper mapper = new RestaurantDataMapper();

    @Test
    void restaurantApprovalRequestToRestaurantMapsCorrectly() {

        RestaurantApprovalRequest restaurantApprovalRequest = RestaurantApprovalRequest.builder()
                .restaurantId(restaurantId.toString())
                .orderId(orderId.toString())
                .products(Collections.singletonList(Product.builder()
                        .productId(new ProductId(productId))
                        .quantity(2)
                        .build()))
                .price(new BigDecimal("100"))
                .restaurantOrderStatus(RestaurantOrderStatus.PAID)
                .build();

        Restaurant restaurant = mapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);

        assertNotNull(restaurant);
        assertEquals(restaurantApprovalRequest.getRestaurantId(), restaurant.getId().getValue().toString());
        assertEquals(restaurantApprovalRequest.getOrderId(), restaurant.getOrderDetail().getId().getValue().toString());
        assertEquals(restaurantApprovalRequest.getPrice(), restaurant.getOrderDetail().getTotalAmount().getAmount());
        assertEquals(restaurantApprovalRequest.getRestaurantOrderStatus().name(), restaurant.getOrderDetail().getOrderStatus().name());
        assertEquals(restaurantApprovalRequest.getProducts().size(), restaurant.getOrderDetail().getProducts().size());
    }

    @Test
    void restaurantApprovalRequestToRestaurantHandlesNullProducts() {
        RestaurantApprovalRequest restaurantApprovalRequest = RestaurantApprovalRequest.builder()
                .restaurantId(restaurantId.toString())
                .orderId(orderId.toString())
                .products(null)
                .price(new BigDecimal("100"))
                .restaurantOrderStatus(RestaurantOrderStatus.PAID)
                .build();

        Restaurant restaurant = mapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);

        assertNotNull(restaurant);
        assertEquals(restaurantApprovalRequest.getRestaurantId(), restaurant.getId().getValue().toString());
        assertEquals(restaurantApprovalRequest.getOrderId(), restaurant.getOrderDetail().getId().getValue().toString());
        assertEquals(restaurantApprovalRequest.getPrice(), restaurant.getOrderDetail().getTotalAmount().getAmount());
        assertEquals(restaurantApprovalRequest.getRestaurantOrderStatus().name(), restaurant.getOrderDetail().getOrderStatus().name());
        assertTrue(restaurant.getOrderDetail().getProducts().isEmpty());
    }

    @Test
    void restaurantApprovalRequestToRestaurantHandlesEmptyProducts() {
        RestaurantApprovalRequest restaurantApprovalRequest = RestaurantApprovalRequest.builder()
                .restaurantId(restaurantId.toString())
                .orderId(orderId.toString())
                .products(Collections.emptyList())
                .price(new BigDecimal("100"))
                .restaurantOrderStatus(RestaurantOrderStatus.PAID)
                .build();
        Restaurant restaurant = mapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);

        assertNotNull(restaurant);
        assertEquals(restaurantApprovalRequest.getRestaurantId(), restaurant.getId().getValue().toString());
        assertEquals(restaurantApprovalRequest.getOrderId(), restaurant.getOrderDetail().getId().getValue().toString());
        assertEquals(restaurantApprovalRequest.getOrderId(), restaurant.getOrderDetail().getId().getValue().toString());
        assertEquals(restaurantApprovalRequest.getPrice(), restaurant.getOrderDetail().getTotalAmount().getAmount());
        assertEquals(restaurantApprovalRequest.getRestaurantOrderStatus().name(), restaurant.getOrderDetail().getOrderStatus().name());
        assertTrue(restaurant.getOrderDetail().getProducts().isEmpty());
    }
}