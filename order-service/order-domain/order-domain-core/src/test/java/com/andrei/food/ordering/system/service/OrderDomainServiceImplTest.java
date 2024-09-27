package com.andrei.food.ordering.system.service;

import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.entity.OrderItem;
import com.andrei.food.ordering.system.service.entity.Product;
import com.andrei.food.ordering.system.service.entity.Restaurant;
import com.andrei.food.ordering.system.service.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.service.event.OrderCreatedEvent;
import com.andrei.food.ordering.system.service.event.OrderPaidEvent;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;
import com.andrei.food.ordering.system.service.exception.OrderDomainException;
import com.andrei.food.ordering.system.service.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class OrderDomainServiceImplTest {

    private DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher;
    private DomainEventPublisher<OrderCancelledEvent> orderCancelledEventDomainEventPublisher;
    private DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher;

    @BeforeEach
    void setUp() {
        orderCreatedEventDomainEventPublisher = mock(DomainEventPublisher.class);
        orderCancelledEventDomainEventPublisher = mock(DomainEventPublisher.class);
        orderPaidEventDomainEventPublisher = mock(DomainEventPublisher.class);
    }

    @Test
    void validateAndInitiateOrderShouldCreateOrderWhenRestaurantIsActiveAndProductsAreValid() {
        // Given
        ProductId productId = new ProductId(UUID.randomUUID());
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Restaurant restaurant = Restaurant.builder()
                .products(List.of(new Product(productId, "product1", new Money(new BigDecimal("10.00")))))
                .active(true).build();
        Order order = Order.builder().items(List.of(OrderItem.builder()
                        .product(new Product(productId, "product1", new Money(new BigDecimal("10.00"))))
                        .price(new Money(new BigDecimal("10.00"))) //OrderItemPrice
                        .quantity(1)
                        .subTotal(new Money(new BigDecimal("10.00")))
                        .build()))
                .price(new Money(new BigDecimal("10.00"))) //OrderPrice
                .build();
        // When
        OrderCreatedEvent event = orderDomainService.validateAndInitiateOrder(order, restaurant);

        // Then
        assertNotNull(event);
        assertEquals(order.getOrderStatus(), OrderStatus.PENDING);
    }

    @Test
    void validateAndInitiateOrderShouldThrowExceptionWhenRestaurantIsNotActive() {
        ProductId productId = new ProductId(UUID.randomUUID());
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .products(List.of(new Product(productId, "product1", new Money(new BigDecimal("10.00")))))
                .active(false).build();
        Order order = Order.builder().build();

        // When
        Exception exception = assertThrows(OrderDomainException.class, () -> {
            orderDomainService.validateAndInitiateOrder(order, restaurant);
        });

        // Then
        assertEquals("Restaurant with id " + restaurant.getId().getValue() + " is currently not active", exception.getMessage());
    }

    @Test
    void validateAndInitiateOrderShouldThrowExceptionWhenOrderIsInitiated() {
        ProductId productId = new ProductId(UUID.randomUUID());
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Restaurant restaurant = Restaurant.builder()
                .products(List.of(new Product(productId, "product1", new Money(new BigDecimal("10.00")))))
                .active(true).build();
        Order order = Order.builder().orderId(new OrderId(UUID.randomUUID())).items(List.of(OrderItem.builder()
                        .product(new Product(productId, "product1", new Money(new BigDecimal("10.00"))))
                        .build()))
                .build();
        // When
        Exception exception = assertThrows(OrderDomainException.class, () -> {
            orderDomainService.validateAndInitiateOrder(order, restaurant);
        });

        // Then
        assertEquals("Order is already initiated", exception.getMessage());
    }

    @Test
    void validateAndInitiateOrderShouldThrowExceptionWhenTotalPriceIsZero() {
        // Given
        ProductId productId = new ProductId(UUID.randomUUID());
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Restaurant restaurant = Restaurant.builder()
                .products(List.of(new Product(productId, "product1", new Money(new BigDecimal("10.00")))))
                .active(true).build();
        Order order = Order.builder().items(List.of(OrderItem.builder()
                        .product(new Product(productId, "product1", new Money(new BigDecimal("10.00"))))
                        .price(new Money(new BigDecimal("10.00"))) //OrderItemPrice
                        .quantity(0)
                        .build()))
                .price(new Money(new BigDecimal("0.00"))) //OrderPrice
                .build();
        // When
        Exception exception = assertThrows(OrderDomainException.class, () -> {
            orderDomainService.validateAndInitiateOrder(order, restaurant);
        });

        // Then
        assertEquals("Total price must be greater than zero", exception.getMessage());
    }

    @Test
    void payOrderShouldReturnPaidEvent() {
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Order order = Order.builder().orderId(new OrderId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PENDING)
        .build();

        // When
        OrderPaidEvent event = orderDomainService.payOrder(order);

        // Then
        assertNotNull(event);
        assertEquals(order.getOrderStatus(), OrderStatus.PAID);
    }

    @Test
    void payOrderShouldThrowExceptionWhenOrderStatusIsNotPending() {
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Order order = Order.builder().orderId(new OrderId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PAID)
                .build();
        // When
        Exception exception = assertThrows(OrderDomainException.class, () -> {
            orderDomainService.payOrder(order);
        });

        // Then
        assertEquals("Order is not in the correct state to be paid", exception.getMessage());
    }

    @Test
    void approveOrderShouldChangeOrderStatusToApproved() {
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Order order = Order.builder().orderId(new OrderId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PAID)
                .build();

        // When
        orderDomainService.ApproveOrder(order);

        // Then
        assertEquals(OrderStatus.APPROVED, order.getOrderStatus());
    }

    @Test
    void approveOrderShouldThrowExceptionWhenOrderStatusIsNotPending() {
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Order order = Order.builder().orderId(new OrderId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PENDING)
                .build();

        // When
        Exception exception = assertThrows(OrderDomainException.class, () -> {
            orderDomainService.ApproveOrder(order);
        });

        // Then
        assertEquals("Order is not in the correct state to be approved", exception.getMessage());
    }

    @Test
    void cancelOrderPaymentShouldReturnCanceledEventWhenOrderPaymentIsCancelled() {
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Order order = Order.builder().orderId(new OrderId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PAID)
                .build();

        // When
        OrderCancelledEvent event = orderDomainService.cancelOrderPayment(order, List.of("Payment failed"));

        // Then
        assertNotNull(event);
        assertEquals(order.getOrderStatus(), OrderStatus.CANCELLING);
    }

    @Test
    void cancelOrderPaymentShouldThrowExceptionWhenOrderStatusIsNotPaid() {
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Order order = Order.builder().orderId(new OrderId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PENDING)
                .build();

        // When
        Exception exception = assertThrows(OrderDomainException.class, () -> {
            orderDomainService.cancelOrderPayment(order, List.of("Payment failed"));
        });

        // Then
        assertEquals("Order is not in the correct state to be cancelled", exception.getMessage());
    }
    @Test
    void cancelOrderShouldChangeOrderStatusToCancelled() {
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Order order = Order.builder().orderId(new OrderId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PENDING)
                .build();

        // When
        orderDomainService.cancelOrder(order, List.of("Customer request"));

        // Then
        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
    }

    @Test
    void cancelOrderShouldThrowExceptionWhenOrderStatusIsCancelingOrPending() {
        OrderDomainServiceImpl orderDomainService = new OrderDomainServiceImpl();
        // Given
        Order order = Order.builder().orderId(new OrderId(UUID.randomUUID()))
                .orderStatus(OrderStatus.PAID)
                .build();

        // When
        Exception exception = assertThrows(OrderDomainException.class, () -> {
            orderDomainService.cancelOrder(order, List.of("Customer request"));
        });

        // Then
        assertEquals("Order is not in the correct state to be cancelled", exception.getMessage());
    }
}
