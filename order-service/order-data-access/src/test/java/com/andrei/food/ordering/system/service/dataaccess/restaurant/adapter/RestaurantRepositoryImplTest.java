package com.andrei.food.ordering.system.service.dataaccess.restaurant.adapter;
import com.andrei.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import com.andrei.food.ordering.system.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.andrei.food.ordering.system.service.entity.Restaurant;
import com.andrei.food.ordering.system.service.valueobject.RestaurantId;
import com.andrei.food.ordering.system.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class RestaurantRepositoryImplTest {

    @Mock
    private RestaurantJpaRepository restaurantJpaRepository;

    @Mock
    private RestaurantDataAccessMapper restaurantDataAccessMapper;

    private RestaurantRepositoryImpl restaurantRepositoryImpl;

    private UUID restaurantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurantRepositoryImpl = new RestaurantRepositoryImpl(restaurantJpaRepository, restaurantDataAccessMapper);
    }

    @Test
    void shouldFindRestaurantInformation() {
        Restaurant restaurant = Restaurant.builder().restaurantId(new RestaurantId(restaurantId)).build();
        List<UUID> restaurantProducts = Arrays.asList(UUID.randomUUID());
        when(restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant)).thenReturn(restaurantProducts);
        when(restaurantJpaRepository.findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), restaurantProducts)).thenReturn(Optional.of(Arrays.asList(new RestaurantEntity())));
        when(restaurantDataAccessMapper.restaurantEntityToRestaurant(List.of(new RestaurantEntity()))).thenReturn(restaurant);

        Optional<Restaurant> foundRestaurant = restaurantRepositoryImpl.findRestaurantInformation(restaurant);

        assertEquals(Optional.of(restaurant), foundRestaurant);
    }

    @Test
    void shouldReturnEmptyWhenRestaurantInformationNotFound() {
        Restaurant restaurant = Restaurant.builder().restaurantId(new RestaurantId(restaurantId)).build();
        List<UUID> restaurantProducts = Arrays.asList(UUID.randomUUID());
        when(restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant)).thenReturn(restaurantProducts);
        when(restaurantJpaRepository.findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), restaurantProducts)).thenReturn(Optional.empty());

        Optional<Restaurant> foundRestaurant = restaurantRepositoryImpl.findRestaurantInformation(restaurant);

        assertEquals(Optional.empty(), foundRestaurant);
    }
}
