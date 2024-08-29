package com.andrei.food.ordering.system.service.domain.mapper;

import com.andrei.food.ordering.system.service.domain.dto.create.CreateOrderCommand;
import com.andrei.food.ordering.system.service.domain.dto.create.CreateOrderResponse;
import com.andrei.food.ordering.system.service.domain.dto.create.OrderAddress;
import com.andrei.food.ordering.system.service.domain.dto.create.OrderItem;
import com.andrei.food.ordering.system.service.domain.dto.track.TrackOrderResponse;
import com.andrei.food.ordering.system.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.entity.Restaurant;
import com.andrei.food.ordering.system.service.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class OrderDataMapperTest {

    @Mock
    private CreateOrderCommand createOrderCommand;

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
}
