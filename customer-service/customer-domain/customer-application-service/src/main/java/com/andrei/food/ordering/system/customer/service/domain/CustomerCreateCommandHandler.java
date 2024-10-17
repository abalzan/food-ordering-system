package com.andrei.food.ordering.system.customer.service.domain;

import com.andrei.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.andrei.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.andrei.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper;
import com.andrei.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
class CustomerCreateCommandHandler {

    private final CustomerDomainService customerDomainService;

    private final CustomerRepository customerRepository;

    private final CustomerDataMapper customerDataMapper;

    @Transactional
    public CustomerCreatedEvent createCustomer(CreateCustomerCommand createCustomerCommand) {
        Customer customer = customerDataMapper.createCustomerCommandToCustomer(createCustomerCommand);
        CustomerCreatedEvent customerCreatedEvent = customerDomainService.validateAndInitiateCustomer(customer);
        Customer savedCustomer = customerRepository.createCustomer(customer);
        if (savedCustomer == null) {
            log.error("Could not save customer with id: {}", createCustomerCommand.customerId());
            throw new CustomerDomainException("Could not save customer with id " +
                    createCustomerCommand.customerId());
        }
        log.info("Returning CustomerCreatedEvent for customer id: {}", createCustomerCommand.customerId());
        return customerCreatedEvent;
    }
}
