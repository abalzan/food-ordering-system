package com.andrei.food.ordering.system.service.dataaccess.restaurant.mapper;

import com.andrei.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import com.andrei.food.ordering.system.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.andrei.food.ordering.system.service.entity.Product;
import com.andrei.food.ordering.system.service.entity.Restaurant;
import com.andrei.food.ordering.system.service.valueobject.Money;
import com.andrei.food.ordering.system.service.valueobject.ProductId;
import com.andrei.food.ordering.system.service.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(product -> product.getId().getValue())
                .toList();
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity = restaurantEntities.stream()
                .findFirst()
                .orElseThrow(() -> new RestaurantDataAccessException("Restaurant not found"));

        List<Product> restaurantProducts = restaurantEntities.stream()
                .map(entity ->
                        new Product(new ProductId(entity.getProductId()),
                                        entity.getProductName(),
                                        new Money(entity.getProductPrice())))
                                .toList();

        return Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .products(restaurantProducts)
                .active(restaurantEntity.getRestaurantActive())
                .build();

    }
}
