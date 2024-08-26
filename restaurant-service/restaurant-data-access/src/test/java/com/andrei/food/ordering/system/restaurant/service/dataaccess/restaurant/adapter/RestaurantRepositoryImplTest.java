package com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import com.andrei.food.ordering.system.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import com.andrei.food.ordering.system.service.valueobject.RestaurantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class RestaurantRepositoryImplTest {

    @Mock
    private RestaurantJpaRepository restaurantJpaRepository;

    @Mock
    private RestaurantDataAccessMapper restaurantDataAccessMapper;

    @InjectMocks
    private RestaurantRepositoryImpl restaurantRepositoryImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findRestaurantInformationReturnsRestaurant() {
        Restaurant restaurant = Restaurant.builder().restaurantId(new RestaurantId(UUID.randomUUID())).build();
        List<UUID> restaurantProducts = List.of(UUID.randomUUID());
        List<RestaurantEntity> restaurantEntities = List.of(mock(RestaurantEntity.class));

        when(restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant)).thenReturn(restaurantProducts);
        when(restaurantJpaRepository.findByRestaurantIdAndProductIdIn(any(), any())).thenReturn(Optional.of(restaurantEntities));
        when(restaurantDataAccessMapper.restaurantEntityToRestaurant(restaurantEntities)).thenReturn(restaurant);

        Optional<Restaurant> result = restaurantRepositoryImpl.findRestaurantInformation(restaurant);

        assertTrue(result.isPresent());
        assertEquals(restaurant, result.get());
    }

    @Test
    void findRestaurantInformationReturnsEmptyWhenNotFound() {
        Restaurant restaurant = Restaurant.builder().restaurantId(new RestaurantId(UUID.randomUUID())).build();
        List<UUID> restaurantProducts = List.of(UUID.randomUUID());

        when(restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant)).thenReturn(restaurantProducts);
        when(restaurantJpaRepository.findByRestaurantIdAndProductIdIn(any(), any())).thenReturn(Optional.empty());

        Optional<Restaurant> result = restaurantRepositoryImpl.findRestaurantInformation(restaurant);

        assertFalse(result.isPresent());
    }
}