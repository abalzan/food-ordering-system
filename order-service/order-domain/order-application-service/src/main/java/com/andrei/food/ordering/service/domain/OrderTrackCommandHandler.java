package com.andrei.food.ordering.service.domain;

import com.andrei.food.ordering.service.domain.dto.track.TrackOrderQuery;
import com.andrei.food.ordering.service.domain.dto.track.TrackOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderTrackCommandHandler {

    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        return null;
    }
}
