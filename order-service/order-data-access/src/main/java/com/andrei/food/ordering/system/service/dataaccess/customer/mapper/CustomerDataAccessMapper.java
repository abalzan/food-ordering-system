package com.andrei.food.ordering.system.service.dataaccess.customer.mapper;

import com.andrei.food.ordering.system.service.entity.Customer;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.service.dataaccess.customer.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {
    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return new Customer(new CustomerId(customerEntity.getId()));
    }
}
