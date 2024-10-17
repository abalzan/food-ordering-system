package com.andrei.food.ordering.system.customer.service.messaging.mapper;

import com.andrei.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.andrei.food.ordering.system.customer.service.domain.entity.Customer;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerMessagingDataMapperTest {

    private CustomerMessagingDataMapper customerMessagingDataMapper;

    @BeforeEach
    void setUp() {
        customerMessagingDataMapper = new CustomerMessagingDataMapper();
    }

    @Test
    void customerCreatedEventToCustomerAvroModelSuccessfully() {
        Customer customer = new Customer(new CustomerId(UUID.randomUUID()), "john_doe", "John", "Doe");
        CustomerCreatedEvent event = new CustomerCreatedEvent(customer, ZonedDateTime.now());

        CustomerAvroModel avroModel = customerMessagingDataMapper.customerCreatedEventToCustomerAvroModel(event);

        assertEquals(customer.getId().getValue(), avroModel.getId());
        assertEquals(customer.getUsername(), avroModel.getUsername());
        assertEquals(customer.getFirstName(), avroModel.getFirstName());
        assertEquals(customer.getLastName(), avroModel.getLastName());
    }
}