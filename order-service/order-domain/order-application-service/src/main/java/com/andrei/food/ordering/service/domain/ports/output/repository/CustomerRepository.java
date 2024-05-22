package com.andrei.food.ordering.service.domain.ports.output.repository;

import com.andrei.food.ordering.system.domain.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Optional<Customer> findCustomer(UUID customerId);
}
