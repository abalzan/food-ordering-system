package com.andrei.food.ordering.system.service.domain.mapper;

import com.andrei.food.ordering.system.service.domain.dto.create.CreateOrderCommand;
import com.andrei.food.ordering.system.service.domain.dto.create.CreateOrderResponse;
import com.andrei.food.ordering.system.service.domain.dto.create.OrderAddress;
import com.andrei.food.ordering.system.service.domain.dto.create.OrderItem;
import com.andrei.food.ordering.system.service.domain.dto.track.TrackOrderResponse;
import com.andrei.food.ordering.system.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.entity.Product;
import com.andrei.food.ordering.system.service.entity.Restaurant;
import com.andrei.food.ordering.system.service.event.OrderCancelledEvent;
import com.andrei.food.ordering.system.service.event.OrderCreatedEvent;
import com.andrei.food.ordering.system.service.event.OrderPaidEvent;
import com.andrei.food.ordering.system.service.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class OrderDataMapperTest {

    @Mock
    private CreateOrderCommand createOrderCommand;

    @Mock
    private OrderCancelledEvent orderCancelledEvent;

    @Mock
    private Order order;

    private OrderDataMapper orderDataMapper;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderDataMapper = new OrderDataMapper();
    }

    @Test
    void shouldMapCreateOrderCommandToRestaurant() {
        UUID restaurantId = UUID.randomUUID();
        when(createOrderCommand.restaurantId()).thenReturn(restaurantId);
        when(createOrderCommand.items()).thenReturn(Arrays.asList(new OrderItem(UUID.randomUUID(), 1, new BigDecimal(10.0), new BigDecimal(10.0))));

        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);

        assertEquals(restaurantId, restaurant.getId().getValue());
        assertEquals(1, restaurant.getProducts().size());
    }

    @Test
    void shouldMapCreateOrderCommandToOrder() {
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        OrderAddress orderAddress = new OrderAddress("Street", "PostalCode", "City");
        when(createOrderCommand.customerId()).thenReturn(customerId);
        when(createOrderCommand.restaurantId()).thenReturn(restaurantId);
        when(createOrderCommand.address()).thenReturn(orderAddress);
        when(createOrderCommand.price()).thenReturn(new BigDecimal(100.0));
        when(createOrderCommand.items()).thenReturn(Arrays.asList(new OrderItem(UUID.randomUUID(), 1, new BigDecimal(10.0), new BigDecimal(10.0))));

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);

        assertEquals(customerId, order.getCustomerId().getValue());
        assertEquals(restaurantId, order.getRestaurantId().getValue());
        assertEquals(new BigDecimal(100.0), order.getPrice().getAmount());
        assertEquals(1, order.getItems().size());
    }

    @Test
    void shouldMapOrderToCreateOrderResponse() {
        Order order = Order.builder()
                .trackingId(new TrackingId(UUID.randomUUID()))
                .orderStatus(OrderStatus.APPROVED)
                .build();

        CreateOrderResponse createOrderResponse = orderDataMapper.orderToCreateOrderResponse(order, "Order created");

        assertEquals(order.getTrackingId().getValue(), createOrderResponse.orderTrackingId());
        assertEquals(order.getOrderStatus(), createOrderResponse.orderStatus());
        assertEquals("Order created", createOrderResponse.message());
    }

    @Test
    void shouldMapOrderToTrackOrderResponse() {
        Order order = Order.builder()
                .trackingId(new TrackingId(UUID.randomUUID()))
                .orderStatus(OrderStatus.APPROVED)
                .failureMessages(Arrays.asList("Failure 1", "Failure 2"))
                .build();

        TrackOrderResponse trackOrderResponse = orderDataMapper.orderToTrackOrderResponse(order);

        assertEquals(order.getTrackingId().getValue(), trackOrderResponse.orderTrackingId());
        assertEquals(order.getOrderStatus(), trackOrderResponse.orderStatus());
        assertEquals(order.getFailureMessages(), trackOrderResponse.failureMessages());
    }

    @Test
    void shouldMapOrderCancelledEventToOrderPaymentEventPayload() {
        Order order = Order.builder()
                .customerId(new CustomerId(UUID.randomUUID()))
                .orderId(new OrderId(UUID.randomUUID()))
                .price(new Money(new BigDecimal("100.0")))
                .build();
        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(order, ZonedDateTime.now());

        OrderPaymentEventPayload payload = orderDataMapper.orderCancelledEventToOrderPaymentEventPayload(orderCancelledEvent);

        assertEquals(order.getCustomerId().getValue().toString(), payload.getCustomerId());
        assertEquals(order.getId().getValue().toString(), payload.getOrderId());
        assertEquals(order.getPrice().getAmount(), payload.getPrice());
        assertEquals(orderCancelledEvent.getCreatedAt(), payload.getCreatedAt());
        assertEquals(PaymentOrderStatus.CANCELLED.name(), payload.getPaymentOrderStatus());
    }


    @Test
    void shouldMapOrderCreatedEventToOrderPaymentEventPayload() {
        Order order = Order.builder()
                .customerId(new CustomerId(UUID.randomUUID()))
                .orderId(new OrderId(UUID.randomUUID()))
                .price(new Money(new BigDecimal("100.0")))
                .build();

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(order, ZonedDateTime.now());

        OrderPaymentEventPayload payload = orderDataMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent);

        assertEquals(order.getCustomerId().getValue().toString(), payload.getCustomerId());
        assertEquals(order.getId().getValue().toString(), payload.getOrderId());
        assertEquals(order.getPrice().getAmount(), payload.getPrice());
        assertEquals(orderCreatedEvent.getCreatedAt(), payload.getCreatedAt());
        assertEquals(PaymentOrderStatus.PENDING.name(), payload.getPaymentOrderStatus());
    }

    @Test
    void shouldMapOrderPaidEventToOrderApprovalEventPayload() {
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        BigDecimal price = new BigDecimal("100.00");
        ZonedDateTime createdAt = ZonedDateTime.now();
        Product product = new Product(new ProductId(UUID.randomUUID()));
        com.andrei.food.ordering.system.service.entity.OrderItem orderItem = com.andrei.food.ordering.system.service.entity.OrderItem.builder()
                .product(product)
                .quantity(1)
                .price(new Money(price))
                .subTotal(new Money(price))
                .build();

        Order order = Order.builder()
                .orderId(new OrderId(orderId))
                .restaurantId(new RestaurantId(restaurantId))
                .price(new Money(price))
                .items(List.of(orderItem))
                .build();

        OrderPaidEvent orderPaidEvent = new OrderPaidEvent(order, createdAt);

        OrderApprovalEventPayload payload = orderDataMapper.orderPaidEventToOrderApprovalEventPayload(orderPaidEvent);

        assertEquals(orderId.toString(), payload.getOrderId());
        assertEquals(restaurantId.toString(), payload.getRestaurantId());
        assertEquals(RestaurantOrderStatus.PAID.name(), payload.getRestaurantOrderStatus());
        assertEquals(1, payload.getProducts().size());
        assertEquals(product.getId().getValue().toString(), payload.getProducts().get(0).getId());
        assertEquals(1, payload.getProducts().get(0).getQuantity());
        assertEquals(price, payload.getPrice());
        assertEquals(createdAt, payload.getCreatedAt());
    }
}
