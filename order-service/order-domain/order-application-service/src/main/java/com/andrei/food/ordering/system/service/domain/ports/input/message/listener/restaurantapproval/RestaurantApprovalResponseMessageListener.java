package com.andrei.food.ordering.system.service.domain.ports.input.message.listener.restaurantapproval;

import com.andrei.food.ordering.system.service.domain.dto.message.RestaurantApprovalResponse;

public interface RestaurantApprovalResponseMessageListener {

    void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse);

    void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);
}
