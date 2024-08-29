package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.service.domain.ports.output.repository.OrderRepository;
import com.andrei.food.ordering.system.service.entity.Order;
import com.andrei.food.ordering.system.service.exception.OrderNotFoundException;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderSagaHelperTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderSagaHelper orderSagaHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findOrderReturnsOrderSuccessfully() {
        Order order = mock(Order.class);
        String orderId = UUID.randomUUID().toString();

        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));

        Order result = orderSagaHelper.findOrder(orderId);

        assertNotNull(result);
        assertEquals(order, result);
    }

    @Test
    void findOrderThrowsOrderNotFoundException() {
        String orderId = UUID.randomUUID().toString();

        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderSagaHelper.findOrder(orderId));

        assertEquals("Order with id " + orderId + " not found", exception.getMessage());
    }

    @Test
    void saveOrderSavesOrderSuccessfully() {
        Order order = mock(Order.class);

        orderSagaHelper.saveOrder(order);

        verify(orderRepository, times(1)).save(order);
    }
}