package com.andrei.food.ordering.system.restaurant.service.domain;

import com.andrei.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Product;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;
import com.andrei.food.ordering.system.service.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.andrei.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;

class RestaurantDomainServiceImplTest {

    private DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher;
    private DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher;

    @BeforeEach
    void setUp() {
        orderApprovedEventDomainEventPublisher = mock(DomainEventPublisher.class);
        orderRejectedEventDomainEventPublisher = mock(DomainEventPublisher.class);
    }
    RestaurantDomainServiceImpl restaurantDomainServiceImpl = new RestaurantDomainServiceImpl();

    @Test
    void orderIsApprovedWhenNoFailureMessages() {
        // Given
        OrderId orderId = new OrderId(UUID.randomUUID());
        RestaurantDomainServiceImpl restaurantDomainServiceImpl = new RestaurantDomainServiceImpl();
        List<String> failureMessages = new ArrayList<>();

        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .orderDetail(OrderDetail.builder()
                        .orderId(orderId)
                        .orderStatus(OrderStatus.PAID)
                        .totalAmount(new Money(new BigDecimal("100.00")))
                        .products(Collections.singletonList(Product.builder()
                                .productId(new ProductId(UUID.randomUUID()))
                                .name("Product")
                                .price(new Money(new BigDecimal("10.00")))
                                .quantity(10)
                                .available(true)
                                .build()))
                        .build())
                .build();

        OrderApprovalEvent event = restaurantDomainServiceImpl.validateOrder(restaurant, failureMessages, orderApprovedEventDomainEventPublisher, orderRejectedEventDomainEventPublisher);

        assertInstanceOf(OrderApprovedEvent.class, event);
        assertEquals(0, event.getFailureMessages().size());
        assertEquals(OrderApprovalStatus.APPROVED, event.getOrderApproval().getApprovalStatus());
    }

    @Test
    void orderIsRejectedWhenFailureMessagesExist() {
        // Given
        OrderId orderId = new OrderId(UUID.randomUUID());
        RestaurantDomainServiceImpl restaurantDomainServiceImpl = new RestaurantDomainServiceImpl();
        List<String> failureMessages = new ArrayList<>();

        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .orderDetail(OrderDetail.builder()
                        .orderId(orderId)
                        .orderStatus(OrderStatus.PENDING)
                        .products(Collections.singletonList(Product.builder()
                                .productId(new ProductId(UUID.randomUUID()))
                                .name("Product")
                                .price(new Money(new BigDecimal("10.00")))
                                .quantity(10)
                                .available(true)
                                .build()))
                        .build())
                .build();

        OrderApprovalEvent event = restaurantDomainServiceImpl.validateOrder(restaurant, failureMessages, orderApprovedEventDomainEventPublisher, orderRejectedEventDomainEventPublisher);

        assertInstanceOf(OrderRejectedEvent.class, event);
        assertEquals(2, event.getFailureMessages().size());
        assertTrue(event.getFailureMessages().contains("Payment is not completed for order " + orderId.getValue()));
        assertTrue(event.getFailureMessages().contains("Total amount is not correct for order " + orderId.getValue()));
        assertEquals(OrderApprovalStatus.REJECTED, event.getOrderApproval().getApprovalStatus());
    }
}