package com.andrei.food.ordering.system.customer.service.dataaccess.adapter;

import com.andrei.food.ordering.system.customer.service.dataaccess.mapper.CustomerDataAccessMapper;
import com.andrei.food.ordering.system.customer.service.dataaccess.repository.CustomerJpaRepository;
import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;

    private final CustomerDataAccessMapper customerDataAccessMapper;

    @Override
    public Customer createCustomer(Customer customer) {
        return customerDataAccessMapper.customerEntityToCustomer(
                customerJpaRepository.save(customerDataAccessMapper.customerToCustomerEntity(customer)));
    }
}