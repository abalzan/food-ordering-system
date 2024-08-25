package com.andrei.food.ordering.system.restaurant.service.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

class RestaurantApprovalRequestMessageListenerImplTest {

    @Mock
    private RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;

    @InjectMocks
    private RestaurantApprovalRequestMessageListenerImpl restaurantApprovalRequestMessageListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void approveOrderSuccessfully() {
        RestaurantApprovalRequest request = RestaurantApprovalRequest.builder()
                .orderId(UUID.randomUUID().toString())
                .build();
        OrderApprovalEvent event = mock(OrderApprovalEvent.class);

        when(restaurantApprovalRequestHelper.persistOrderApproval(request)).thenReturn(event);

        restaurantApprovalRequestMessageListener.approveOrder(request);

        verify(event).fire();
    }

    @Test
    void approveOrderThrowsException() {
        RestaurantApprovalRequest request = RestaurantApprovalRequest.builder()
                .orderId(UUID.randomUUID().toString())
                .build();

        when(restaurantApprovalRequestHelper.persistOrderApproval(request)).thenThrow(new RestaurantNotFoundException("Restaurant not found"));

        assertThrows(RestaurantNotFoundException.class, () -> restaurantApprovalRequestMessageListener.approveOrder(request));
    }
}