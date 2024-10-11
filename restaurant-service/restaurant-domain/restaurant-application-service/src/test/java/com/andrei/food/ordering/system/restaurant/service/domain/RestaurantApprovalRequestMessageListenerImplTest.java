package com.andrei.food.ordering.system.restaurant.service.domain;

import com.andrei.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

class RestaurantApprovalRequestMessageListenerImplTest {

    @Mock
    private RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;

    @InjectMocks
    private RestaurantApprovalRequestMessageListenerImpl restaurantApprovalRequestMessageListenerImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Approves order successfully")
    void approvesOrderSuccessfully() {
        RestaurantApprovalRequest restaurantApprovalRequest = RestaurantApprovalRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();

        doNothing().when(restaurantApprovalRequestHelper).persistOrderApproval(restaurantApprovalRequest);

        restaurantApprovalRequestMessageListenerImpl.approveOrder(restaurantApprovalRequest);

        verify(restaurantApprovalRequestHelper).persistOrderApproval(restaurantApprovalRequest);
    }
}