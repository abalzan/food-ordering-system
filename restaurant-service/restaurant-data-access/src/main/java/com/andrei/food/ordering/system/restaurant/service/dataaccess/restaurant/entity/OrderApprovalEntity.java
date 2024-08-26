package com.andrei.food.ordering.system.restaurant.service.dataaccess.restaurant.entity;

import com.andrei.food.ordering.system.service.valueobject.OrderApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_approval", schema = "restaurant")
@Entity
public class OrderApprovalEntity {
    @Id
    private UUID id;
    private UUID orderId;
    private UUID restaurantId;
    @Enumerated(EnumType.STRING)
    private OrderApprovalStatus status;
}
