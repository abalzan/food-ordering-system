package com.andrei.food.ordering.system.service.dataaccess.restaurant.repository;

import com.andrei.food.ordering.system.service.dataaccess.restaurant.entity.RestaurantEntity;
import com.andrei.food.ordering.system.service.dataaccess.restaurant.entity.RestaurantEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, RestaurantEntityId> {

    Optional<List<RestaurantEntity>> findByRestaurantIdAndProductIdIn(UUID restaurantId, List<UUID> productIds);
}
