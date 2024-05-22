package com.andrei.food.ordering.service.domain.ports.input.service;

import com.andrei.food.ordering.service.domain.dto.create.CreateOrderCommand;
import com.andrei.food.ordering.service.domain.dto.create.CreateOrderResponse;
import com.andrei.food.ordering.service.domain.dto.track.TrackOrderQuery;
import com.andrei.food.ordering.service.domain.dto.track.TrackOrderResponse;
import jakarta.validation.Valid;

public interface OrderApplicationService {
    CreateOrderResponse createOder(@Valid CreateOrderCommand createOrderCommand);

    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);

}
