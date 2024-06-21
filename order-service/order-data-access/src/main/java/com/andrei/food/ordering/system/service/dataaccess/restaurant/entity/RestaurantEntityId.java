package com.andrei.food.ordering.system.service.dataaccess.restaurant.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RestaurantEntityId implements Serializable {

    private UUID restaurantId;

    private UUID productId;
}
