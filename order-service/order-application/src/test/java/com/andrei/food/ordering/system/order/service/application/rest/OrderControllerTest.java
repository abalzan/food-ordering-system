package com.andrei.food.ordering.system.order.service.application.rest;

import com.andrei.food.ordering.service.domain.dto.create.CreateOrderCommand;
import com.andrei.food.ordering.service.domain.dto.create.CreateOrderResponse;
import com.andrei.food.ordering.service.domain.dto.track.TrackOrderQuery;
import com.andrei.food.ordering.service.domain.dto.track.TrackOrderResponse;
import com.andrei.food.ordering.service.domain.ports.input.service.OrderApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class OrderControllerTest {

    @Mock
    private OrderApplicationService orderApplicationService;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderController(orderApplicationService);
    }

    @Test
    void shouldCreateOrder() {
        UUID customerId = UUID.randomUUID();
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder().customerId(customerId).build();
        CreateOrderResponse createOrderResponse = CreateOrderResponse.builder().orderTrackingId(UUID.randomUUID()).build();
        when(orderApplicationService.createOrder(createOrderCommand)).thenReturn(createOrderResponse);

        ResponseEntity<CreateOrderResponse> responseEntity = orderController.createOrder(createOrderCommand);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(createOrderResponse, responseEntity.getBody());
    }

    @Test
    void shouldGetOrder() {
        UUID trackingId = UUID.randomUUID();
        TrackOrderQuery trackOrderQuery = TrackOrderQuery.builder().orderTrackingId(trackingId).build();
        TrackOrderResponse trackOrderResponse = TrackOrderResponse.builder().orderTrackingId(trackingId).build();

        when(orderApplicationService.trackOrder(trackOrderQuery)).thenReturn(trackOrderResponse);

        ResponseEntity<TrackOrderResponse> responseEntity = orderController.getOrder(trackingId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(trackOrderResponse, responseEntity.getBody());
    }
}
