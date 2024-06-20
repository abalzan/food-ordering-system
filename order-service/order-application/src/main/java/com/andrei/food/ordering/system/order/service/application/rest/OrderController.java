package com.andrei.food.ordering.system.order.service.application.rest;

import com.andrei.food.ordering.service.domain.dto.create.CreateOrderCommand;
import com.andrei.food.ordering.service.domain.dto.create.CreateOrderResponse;
import com.andrei.food.ordering.service.domain.dto.track.TrackOrderQuery;
import com.andrei.food.ordering.service.domain.dto.track.TrackOrderResponse;
import com.andrei.food.ordering.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/orders", produces = "application/vnd.api.v1+json")
public class OrderController {
    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderCommand createOrderCommand) {
        log.info("Creating order for customer: {}", createOrderCommand.customerId());
        CreateOrderResponse orderResponse = orderApplicationService.createOrder(createOrderCommand);
        log.info("Order created with tracking Id: {}", orderResponse.orderTrackingId());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<TrackOrderResponse> getOrder(@PathVariable UUID trackingId) {
        log.info("Getting order with tracking Id: {}", trackingId);
        TrackOrderResponse trackOrderResponse = orderApplicationService.trackOrder(TrackOrderQuery.builder().orderTrackingId(trackingId).build());
        log.info("Returning order with tracking Id: {}", trackOrderResponse.orderTrackingId());
        return ResponseEntity.status(HttpStatus.OK).body(trackOrderResponse);
    }
}
