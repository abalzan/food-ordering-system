package com.andrei.food.ordering.service.domain;

import com.andrei.food.ordering.service.domain.dto.track.TrackOrderQuery;
import com.andrei.food.ordering.service.domain.dto.track.TrackOrderResponse;
import com.andrei.food.ordering.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.service.domain.ports.output.repository.OrderRepository;
import com.andrei.food.ordering.system.domain.exception.OrderDomainException;
import com.andrei.food.ordering.system.domain.exception.OrderNotFoundException;
import com.andrei.food.ordering.system.domain.valueobject.TrackingId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
public class OrderTrackCommandHandler {

    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        return orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.orderTrackingId()))
                .map(orderDataMapper::orderToTrackOrderResponse)
                .orElseThrow(() -> new OrderNotFoundException("Order not found. Tracking id: " + trackOrderQuery.orderTrackingId()));
    }
}
