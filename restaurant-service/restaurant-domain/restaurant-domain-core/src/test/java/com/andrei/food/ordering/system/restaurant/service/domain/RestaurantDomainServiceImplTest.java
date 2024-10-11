package com.andrei.food.ordering.system.restaurant.service.domain;

import com.andrei.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import com.andrei.food.ordering.system.service.valueobject.*;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RestaurantDomainServiceImplTest {

    @InjectMocks
    private RestaurantDomainServiceImpl restaurantDomainService;

    private  LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logCaptor = LogCaptor.forClass(RestaurantDomainServiceImpl.class);
    }

    @DisplayName("Validates order and approves when no failure messages")
    @Test
    void validateOrderApprovesWhenNoFailureMessages() {
        Restaurant restaurant = mock(Restaurant.class);
        List<String> failureMessages = new ArrayList<>();
        OrderDetail orderDetail = mock(OrderDetail.class);
        when(orderDetail.getId()).thenReturn(mock(OrderId.class));
        when(orderDetail.getId().getValue()).thenReturn(UUID.randomUUID());
        when(orderDetail.getProducts()).thenReturn(Collections.emptyList());
        when(restaurant.getOrderDetail()).thenReturn(orderDetail);
        when(restaurant.getId()).thenReturn(new RestaurantId(UUID.randomUUID()));

        OrderApprovalEvent event = restaurantDomainService.validateOrder(restaurant, failureMessages);

        assertTrue(event instanceof OrderApprovedEvent);
        verify(restaurant).constructOrderApproval(OrderApprovalStatus.APPROVED);
    }

    @DisplayName("Validates order and rejects when there are failure messages")
    @Test
    void validateOrderRejectsWhenThereAreFailureMessages() {
        Restaurant restaurant = mock(Restaurant.class);
        List<String> failureMessages = new ArrayList<>();
        failureMessages.add("Failure message");
        OrderDetail orderDetail = mock(OrderDetail.class);
        when(orderDetail.getId()).thenReturn(mock(OrderId.class));
        when(orderDetail.getId().getValue()).thenReturn(UUID.randomUUID());
        when(orderDetail.getProducts()).thenReturn(Collections.emptyList());
        when(restaurant.getOrderDetail()).thenReturn(orderDetail);
        when(restaurant.getId()).thenReturn(new RestaurantId(UUID.randomUUID()));

        OrderApprovalEvent event = restaurantDomainService.validateOrder(restaurant, failureMessages);

        assertTrue(event instanceof OrderRejectedEvent);
        verify(restaurant).constructOrderApproval(OrderApprovalStatus.REJECTED);
    }

    @DisplayName("Logs order validation with correct order id")
    @Test
    void logsOrderValidationWithCorrectOrderId() {
        OrderDetail orderDetail = OrderDetail.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .products(Collections.emptyList())
                .build();
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .orderDetail(orderDetail)
                .build();
        List<String> failureMessages = new ArrayList<>();

        restaurantDomainService.validateOrder(restaurant, failureMessages);

        assertEquals("Validating order with id: " + orderDetail.getId().getValue(), logCaptor.getInfoLogs().get(0));
    }

    @DisplayName("Logs order approval with correct restaurant id")
    @Test
    void logsOrderApprovalWithCorrectRestaurantId() {
        OrderDetail orderDetail = OrderDetail.builder()
                .orderStatus(OrderStatus.PAID)
                .orderId(new OrderId(UUID.randomUUID()))
                .products(Collections.emptyList())
                .totalAmount(new Money(new BigDecimal("0")))
                .build();
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .orderDetail(orderDetail)
                .build();
        List<String> failureMessages = new ArrayList<>();

        restaurantDomainService.validateOrder(restaurant, failureMessages);

        assertEquals("Order approved for restaurant with id: " + restaurant.getId().getValue(), logCaptor.getInfoLogs().get(1));
    }

    @DisplayName("Logs order rejection with correct restaurant id")
    @Test
    void logsOrderRejectionWithCorrectRestaurantId() {
        OrderDetail orderDetail = OrderDetail.builder()
                .orderStatus(OrderStatus.PAID)
                .orderId(new OrderId(UUID.randomUUID()))
                .products(Collections.emptyList())
                .build();
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .orderDetail(orderDetail)
                .build();
        List<String> failureMessages = new ArrayList<>();

        restaurantDomainService.validateOrder(restaurant, failureMessages);

        assertEquals("Order rejected for restaurant with id: " + restaurant.getId().getValue(), logCaptor.getInfoLogs().get(1));
    }
}