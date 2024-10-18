package com.andrei.food.ordering.system.service.domain;

import com.andrei.food.ordering.system.service.domain.dto.message.CustomerModel;
import com.andrei.food.ordering.system.service.domain.mapper.OrderDataMapper;
import com.andrei.food.ordering.system.service.domain.ports.input.message.listener.customer.CustomerMessageListener;
import com.andrei.food.ordering.system.service.domain.ports.output.repository.CustomerRepository;
import com.andrei.food.ordering.system.service.entity.Customer;
import com.andrei.food.ordering.system.service.exception.OrderDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerMessageImpl implements CustomerMessageListener {

    private final CustomerRepository customerRepository;
    private final OrderDataMapper orderDataMapper;

    @Override
    public void customerCreated(CustomerModel customerModel) {
        log.info("Customer created: {}", customerModel);
        Customer customer = customerRepository.save(orderDataMapper.customerModelToCustomer(customerModel));
        if(customer == null) {
         log.error("Customer could not be created in order database with id {}", customerModel.id());
         throw new OrderDomainException("Customer could not be created in order database with id " + customerModel.id());
        }
        log.info("Customer created in order database with id {}", customerModel.id());

    }
}
