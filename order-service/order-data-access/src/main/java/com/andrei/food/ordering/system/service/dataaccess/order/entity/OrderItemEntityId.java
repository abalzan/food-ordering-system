package com.andrei.food.ordering.system.service.dataaccess.order.entity;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class OrderItemEntityId implements Serializable {
    private Long id;
    private OrderEntity order;

}
