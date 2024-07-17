package com.andrei.food.ordering.system.service.domain.ports.output.repository;

import com.andrei.food.ordering.system.service.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {
    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);

}
