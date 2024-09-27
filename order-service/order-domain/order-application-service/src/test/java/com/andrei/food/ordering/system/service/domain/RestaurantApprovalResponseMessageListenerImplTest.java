package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.service.domain.dto.message.RestaurantApprovalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantApprovalResponseMessageListenerImplTest {

    @Mock
    private OrderApprovalSaga orderApprovalSaga;

    @InjectMocks
    private RestaurantApprovalResponseMessageListenerImpl restaurantApprovalResponseMessageListenerImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void orderApprovedProcessesSuccessfully() {
        RestaurantApprovalResponse restaurantApprovalResponse = mock(RestaurantApprovalResponse.class);

        restaurantApprovalResponseMessageListenerImpl.orderApproved(restaurantApprovalResponse);

        verify(orderApprovalSaga, times(1)).process(restaurantApprovalResponse);
    }

    @Test
    void orderApprovedHandlesNullResponseGracefully() {
        assertThrows(NullPointerException.class, () -> restaurantApprovalResponseMessageListenerImpl.orderApproved(null));
    }

    @Test
    void orderRejectedRollsBackSuccessfully() {
        RestaurantApprovalResponse restaurantApprovalResponse = mock(RestaurantApprovalResponse.class);

        restaurantApprovalResponseMessageListenerImpl.orderRejected(restaurantApprovalResponse);

        verify(orderApprovalSaga, times(1)).rollback(restaurantApprovalResponse);
    }

    @Test
    void orderRejectedHandlesNullResponseGracefully() {
        assertThrows(NullPointerException.class, () -> restaurantApprovalResponseMessageListenerImpl.orderRejected(null));
    }
}