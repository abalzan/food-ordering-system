package com.andrei.food.ordering.system.customer.service.domain.mapper;

import com.andrei.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.andrei.food.ordering.system.customer.service.domain.create.CreateCustomerResponse;
import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataMapper {
    public Customer createCustomerCommandToCustomer(CreateCustomerCommand createCustomerCommand) {
        return new Customer(new CustomerId(createCustomerCommand.customerId()),
                createCustomerCommand.username(),
                createCustomerCommand.firstName(),
                createCustomerCommand.lastName());
    }

    public CreateCustomerResponse customerToCreateCustomerResponse(Customer customer, String message) {
        return new CreateCustomerResponse(customer.getId().getValue().toString(), message);
    }
}
