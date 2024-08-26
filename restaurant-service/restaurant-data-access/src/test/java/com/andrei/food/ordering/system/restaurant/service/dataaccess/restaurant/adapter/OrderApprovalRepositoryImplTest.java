package com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.adapter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.entity.OrderApprovalEntity;
import com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.repository.OrderApprovalJpaRepository;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class OrderApprovalRepositoryImplTest {

    @Mock
    private OrderApprovalJpaRepository orderApprovalJpaRepository;

    @Mock
    private RestaurantDataAccessMapper restaurantDataAccessMapper;

    @InjectMocks
    private OrderApprovalRepositoryImpl orderApprovalRepositoryImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveOrderApprovalSuccessfully() {
        OrderApproval orderApproval = mock(OrderApproval.class);
        OrderApprovalEntity orderApprovalEntity = mock(OrderApprovalEntity.class);

        when(restaurantDataAccessMapper.orderApprovalToOrderApprovalEntity(orderApproval)).thenReturn(orderApprovalEntity);
        when(orderApprovalJpaRepository.save(orderApprovalEntity)).thenReturn(orderApprovalEntity);
        when(restaurantDataAccessMapper.orderApprovalEntityToOrderApproval(orderApprovalEntity)).thenReturn(orderApproval);

        OrderApproval result = orderApprovalRepositoryImpl.save(orderApproval);

        assertNotNull(result);
        assertEquals(orderApproval, result);
    }
}