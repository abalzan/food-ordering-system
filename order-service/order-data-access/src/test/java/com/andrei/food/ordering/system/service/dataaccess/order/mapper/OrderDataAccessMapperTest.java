package com.andrei.food.ordering.system.service.dataaccess.order.mapper;

import com.andrei.food.ordering.system.domain.entity.Order;
import com.andrei.food.ordering.system.domain.entity.OrderItem;
import com.andrei.food.ordering.system.domain.entity.Product;
import com.andrei.food.ordering.system.domain.valueobject.*;
import com.andrei.food.ordering.system.service.dataaccess.order.entity.OrderAddressEntity;
import com.andrei.food.ordering.system.service.dataaccess.order.entity.OrderEntity;
import com.andrei.food.ordering.system.service.dataaccess.order.entity.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class OrderDataAccessMapperTest {

    private final UUID trackingId = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();
    private final UUID restaurantId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final UUID streetAddressId = UUID.randomUUID();

    @Mock
    private Order order;

    @Mock
    private OrderEntity orderEntity;

    private OrderDataAccessMapper orderDataAccessMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderDataAccessMapper = new OrderDataAccessMapper();
    }

    @Test
    void shouldMapOrderToOrderEntity() {
        when(order.getId()).thenReturn(new OrderId(orderId));
        when(order.getCustomerId()).thenReturn(new CustomerId(customerId));
        when(order.getRestaurantId()).thenReturn(new RestaurantId(restaurantId));
        when(order.getTrackingId()).thenReturn(new TrackingId(trackingId));
        when(order.getDeliveryAddress()).thenReturn(new StreetAddress(streetAddressId, "Street", "City", "PostalCode"));
        when(order.getPrice()).thenReturn(new Money(new BigDecimal(100.0)));
        when(order.getItems()).thenReturn(Arrays.asList(OrderItem.Builder.builder()
                .orderItemId(new OrderItemId(123L))
                .product(new Product(new ProductId(UUID.randomUUID())))
                .quantity(2)
                .price(new Money(new BigDecimal(50.0)))
                .subTotal(new Money(new BigDecimal(100.0)))
                .build()));
        when(order.getOrderStatus()).thenReturn(OrderStatus.APPROVED);
        when(order.getFailureMessages()).thenReturn(Arrays.asList("Failure 1", "Failure 2"));

        OrderEntity mappedOrderEntity = orderDataAccessMapper.orderToOrderEntity(order);

        assertEquals(order.getId().getValue(), mappedOrderEntity.getId());
        assertEquals(order.getCustomerId().getValue(), mappedOrderEntity.getCustomerId());
        assertEquals(order.getRestaurantId().getValue(), mappedOrderEntity.getRestaurantId());
        assertEquals(order.getTrackingId().getValue(), mappedOrderEntity.getTrackingId());
        assertEquals(order.getDeliveryAddress().getCity(), mappedOrderEntity.getAddress().getCity());
        assertEquals(order.getPrice().getAmount(), mappedOrderEntity.getPrice());
        assertEquals(order.getItems().size(), mappedOrderEntity.getItems().size());
        assertEquals(order.getOrderStatus(), mappedOrderEntity.getOrderStatus());
        assertEquals(String.join(Order.FAILURE_MESSAGE_DELIMITER, order.getFailureMessages()), mappedOrderEntity.getFailureMessages());
    }

    @Test
    void shouldMapOrderEntityToOrder() {
        when(orderEntity.getId()).thenReturn(orderId);
        when(orderEntity.getCustomerId()).thenReturn(customerId);
        when(orderEntity.getRestaurantId()).thenReturn(restaurantId);
        when(orderEntity.getTrackingId()).thenReturn(trackingId);
        when(orderEntity.getAddress()).thenReturn(OrderAddressEntity.builder()
                        .id(streetAddressId)
                        .street("Street")
                        .city("City")
                        .postalCode("PostalCode")
                .build());
        when(orderEntity.getPrice()).thenReturn(new BigDecimal(100.0));
        when(orderEntity.getItems()).thenReturn(Arrays.asList(OrderItemEntity.builder()
                .id(123L)
                .productId(UUID.randomUUID())
                .quantity(2)
                .price(new BigDecimal(50.0))
                .subTotal(new BigDecimal(100.0))
                .build()));
        when(orderEntity.getOrderStatus()).thenReturn(OrderStatus.APPROVED);
        when(orderEntity.getFailureMessages()).thenReturn("Failure 1" + Order.FAILURE_MESSAGE_DELIMITER + "Failure 2");

        Order mappedOrder = orderDataAccessMapper.orderEntityToOrder(orderEntity);

        assertEquals(orderEntity.getId(), mappedOrder.getId().getValue());
        assertEquals(orderEntity.getCustomerId(), mappedOrder.getCustomerId().getValue());
        assertEquals(orderEntity.getRestaurantId(), mappedOrder.getRestaurantId().getValue());
        assertEquals(orderEntity.getTrackingId(), mappedOrder.getTrackingId().getValue());
        assertEquals(orderEntity.getAddress().getStreet(), mappedOrder.getDeliveryAddress().getStreet());
        assertEquals(orderEntity.getPrice(), mappedOrder.getPrice().getAmount());
        assertEquals(orderEntity.getItems().size(), mappedOrder.getItems().size());
        assertEquals(orderEntity.getOrderStatus(), mappedOrder.getOrderStatus());
        assertEquals(Arrays.asList(orderEntity.getFailureMessages().split(Order.FAILURE_MESSAGE_DELIMITER)), mappedOrder.getFailureMessages());
    }
}
