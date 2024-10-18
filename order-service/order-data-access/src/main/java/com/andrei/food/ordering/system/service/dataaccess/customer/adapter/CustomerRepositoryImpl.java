package com.andrei.food.ordering.system.service.dataaccess.customer.adapter;

import com.andrei.food.ordering.system.service.domain.ports.output.repository.CustomerRepository;
import com.andrei.food.ordering.system.service.entity.Customer;
import com.andrei.food.ordering.system.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import com.andrei.food.ordering.system.service.dataaccess.customer.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;

    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerJpaRepository.findById(customerId).map(customerDataAccessMapper::customerEntityToCustomer);
    }

    @Transactional
    @Override
    public Customer save(Customer customer) {
        return customerDataAccessMapper.customerEntityToCustomer(
                customerJpaRepository.save(customerDataAccessMapper.customerToCustomerEntity(customer)));
    }
}
