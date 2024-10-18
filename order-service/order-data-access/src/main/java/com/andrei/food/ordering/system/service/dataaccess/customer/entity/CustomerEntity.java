package com.andrei.food.ordering.system.service.dataaccess.customer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "customers")
@Entity
public class CustomerEntity {
    @Id
    private UUID id;
    private String userName,
            firstName,
            lastName;
}
