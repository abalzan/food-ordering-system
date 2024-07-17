package com.andrei.food.ordering.system.service.dataaccess.restaurant.adapter;

import com.andrei.food.ordering.system.service.domain.ports.output.repository.RestaurantRepository;
import com.andrei.food.ordering.system.service.entity.Restaurant;
import com.andrei.food.ordering.system.service.dataaccess.restaurant.entity.RestaurantEntity;
import com.andrei.food.ordering.system.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.andrei.food.ordering.system.service.dataaccess.restaurant.repository.RestaurantJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {
    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository restaurantJpaRepository, RestaurantDataAccessMapper restaurantDataAccessMapper) {
        this.restaurantJpaRepository = restaurantJpaRepository;
        this.restaurantDataAccessMapper = restaurantDataAccessMapper;
    }

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        List<UUID> restaurantProducts = restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);
        Optional<List<RestaurantEntity>> restaurantEntities = restaurantJpaRepository.findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), restaurantProducts);

        return restaurantEntities.map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
    }
}
