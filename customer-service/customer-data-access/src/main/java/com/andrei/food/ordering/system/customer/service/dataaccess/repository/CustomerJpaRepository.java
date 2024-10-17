package com.andrei.food.ordering.system.customer.service.dataaccess.repository;

import com.andrei.food.ordering.system.customer.service.dataaccess.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, String> {
}
