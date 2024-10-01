package com.andrei.food.ordering.system.service.dataaccess.restaurant.mapper;

import com.andrei.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import com.andrei.food.ordering.system.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.andrei.food.ordering.system.service.entity.Product;
import com.andrei.food.ordering.system.service.entity.Restaurant;
import com.andrei.food.ordering.system.service.valueobject.Money;
import com.andrei.food.ordering.system.service.valueobject.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantDataAccessMapperTest {

    private RestaurantDataAccessMapper restaurantDataAccessMapper;

    @BeforeEach
    void setUp() {
        restaurantDataAccessMapper = new RestaurantDataAccessMapper();
    }

    @Test
    void restaurantToRestaurantProductsMapsCorrectly() {
        Restaurant restaurant = Restaurant.builder()
                .products(List.of(
                        new Product(new ProductId(UUID.randomUUID()), "Product1", new Money(new BigDecimal(100))),
                        new Product(new ProductId(UUID.randomUUID()), "Product2", new Money(new BigDecimal(200)))
                ))
                .build();

        List<UUID> productIds = restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);

        assertEquals(2, productIds.size());
    }

    @Test
    void restaurantEntityToRestaurantMapsCorrectly() {
        RestaurantEntity entity1 = new RestaurantEntity();
        entity1.setRestaurantId(UUID.randomUUID());
        entity1.setProductId(UUID.randomUUID());
        entity1.setProductName("Product1");
        entity1.setProductPrice(new BigDecimal(100));
        entity1.setRestaurantActive(true);

        RestaurantEntity entity2 = new RestaurantEntity();
        entity2.setRestaurantId(entity1.getRestaurantId());
        entity2.setProductId(UUID.randomUUID());
        entity2.setProductName("Product2");
        entity2.setProductPrice(new BigDecimal(200));
        entity2.setRestaurantActive(true);

        List<RestaurantEntity> entities = List.of(entity1, entity2);

        Restaurant restaurant = restaurantDataAccessMapper.restaurantEntityToRestaurant(entities);

        assertEquals(entity1.getRestaurantId(), restaurant.getId().getValue());
        assertEquals(2, restaurant.getProducts().size());
        assertTrue(restaurant.isActive());
    }

    @Test
    void restaurantEntityToRestaurantThrowsExceptionWhenNotFound() {
        List<RestaurantEntity> entities = List.of();

        assertThrows(RestaurantDataAccessException.class, () -> restaurantDataAccessMapper.restaurantEntityToRestaurant(entities));
    }
}