package com.andrei.food.ordering.system.service.dataaccess.customer.repository;

import com.andrei.food.ordering.system.domain.entity.Customer;
import com.andrei.food.ordering.system.service.dataaccess.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<Customer> findCustomer(UUID customerId);
}
