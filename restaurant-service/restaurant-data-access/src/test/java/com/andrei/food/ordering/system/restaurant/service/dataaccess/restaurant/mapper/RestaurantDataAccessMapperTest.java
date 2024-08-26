package com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import com.andrei.food.ordering.system.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.entity.OrderApprovalEntity;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Product;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.andrei.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;
import com.andrei.food.ordering.system.service.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

class RestaurantDataAccessMapperTest {

    @InjectMocks
    private RestaurantDataAccessMapper restaurantDataAccessMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void restaurantToRestaurantProductsReturnsProductIds() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(new ProductId(UUID.randomUUID()));
        Restaurant restaurant = mock(Restaurant.class);
        OrderDetail orderDetail = mock(OrderDetail.class);
        when(orderDetail.getProducts()).thenReturn(List.of(product));
        when(restaurant.getOrderDetail()).thenReturn(orderDetail);

        List<UUID> result = restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product.getId().getValue(), result.get(0));
    }

    @Test
    void restaurantEntityToRestaurantReturnsRestaurant() {
        RestaurantEntity restaurantEntity = mock(RestaurantEntity.class);
        when(restaurantEntity.getRestaurantId()).thenReturn(UUID.randomUUID());
        when(restaurantEntity.getProductId()).thenReturn(UUID.randomUUID());
        when(restaurantEntity.getProductName()).thenReturn("Product Name");
        when(restaurantEntity.getProductPrice()).thenReturn(new BigDecimal("100.00"));
        when(restaurantEntity.getProductAvailable()).thenReturn(true);
        when(restaurantEntity.getRestaurantActive()).thenReturn(true);

        List<RestaurantEntity> restaurantEntities = List.of(restaurantEntity);

        Restaurant result = restaurantDataAccessMapper.restaurantEntityToRestaurant(restaurantEntities);

        assertNotNull(result);
        assertEquals(restaurantEntity.getRestaurantId(), result.getId().getValue());
        assertEquals(1, result.getOrderDetail().getProducts().size());
        assertEquals(restaurantEntity.getProductId(), result.getOrderDetail().getProducts().get(0).getId().getValue());
    }

    @Test
    void restaurantEntityToRestaurantThrowsExceptionWhenNoEntities() {
        List<RestaurantEntity> restaurantEntities = List.of();

        assertThrows(RestaurantDataAccessException.class, () -> restaurantDataAccessMapper.restaurantEntityToRestaurant(restaurantEntities));
    }

    @Test
    void orderApprovalToOrderApprovalEntityReturnsEntity() {
        OrderApproval orderApproval = mock(OrderApproval.class);
        when(orderApproval.getId()).thenReturn(new OrderApprovalId(UUID.randomUUID()));
        when(orderApproval.getRestaurantId()).thenReturn(new RestaurantId(UUID.randomUUID()));
        when(orderApproval.getOrderId()).thenReturn(new OrderId(UUID.randomUUID()));
        when(orderApproval.getApprovalStatus()).thenReturn(OrderApprovalStatus.APPROVED);

        OrderApprovalEntity result = restaurantDataAccessMapper.orderApprovalToOrderApprovalEntity(orderApproval);

        assertNotNull(result);
        assertEquals(orderApproval.getId().getValue(), result.getId());
        assertEquals(orderApproval.getRestaurantId().getValue(), result.getRestaurantId());
        assertEquals(orderApproval.getOrderId().getValue(), result.getOrderId());
        assertEquals(orderApproval.getApprovalStatus(), result.getStatus());
    }

    @Test
    void orderApprovalEntityToOrderApprovalReturnsOrderApproval() {
        OrderApprovalEntity orderApprovalEntity = mock(OrderApprovalEntity.class);
        when(orderApprovalEntity.getId()).thenReturn(UUID.randomUUID());
        when(orderApprovalEntity.getRestaurantId()).thenReturn(UUID.randomUUID());
        when(orderApprovalEntity.getOrderId()).thenReturn(UUID.randomUUID());
        when(orderApprovalEntity.getStatus()).thenReturn(OrderApprovalStatus.APPROVED);

        OrderApproval result = restaurantDataAccessMapper.orderApprovalEntityToOrderApproval(orderApprovalEntity);

        assertNotNull(result);
        assertEquals(orderApprovalEntity.getId(), result.getId().getValue());
        assertEquals(orderApprovalEntity.getRestaurantId(), result.getRestaurantId().getValue());
        assertEquals(orderApprovalEntity.getOrderId(), result.getOrderId().getValue());
        assertEquals(orderApprovalEntity.getStatus(), result.getApprovalStatus());
    }
}