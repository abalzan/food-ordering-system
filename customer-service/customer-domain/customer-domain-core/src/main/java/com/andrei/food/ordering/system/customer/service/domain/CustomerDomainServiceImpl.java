package com.andrei.food.ordering.system.customer.service.domain;

import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.andrei.food.ordering.system.service.DomainConstants;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
public class CustomerDomainServiceImpl implements CustomerDomainService{

    @Override
    public CustomerCreatedEvent validateAndInitiateCustomer(Customer customer) {
        //TODO: Implement validation logic
      log.info("Validating and initiating customer: {}", customer.getId().getValue());
        return new CustomerCreatedEvent(customer, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)));
    }
}

