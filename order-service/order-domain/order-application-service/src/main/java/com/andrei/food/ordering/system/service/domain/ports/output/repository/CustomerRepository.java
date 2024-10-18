package com.andrei.food.ordering.system.service.domain.ports.output.repository;

import com.andrei.food.ordering.system.service.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Optional<Customer> findCustomer(UUID customerId);

    Customer save(Customer customer);
}
