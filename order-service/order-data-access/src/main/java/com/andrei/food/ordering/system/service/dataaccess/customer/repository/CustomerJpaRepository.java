package com.andrei.food.ordering.system.service.dataaccess.customer.repository;

import com.andrei.food.ordering.system.service.entity.Customer;
import com.andrei.food.ordering.system.service.dataaccess.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
}
