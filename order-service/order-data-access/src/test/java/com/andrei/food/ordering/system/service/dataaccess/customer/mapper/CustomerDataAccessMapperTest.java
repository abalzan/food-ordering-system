package com.andrei.food.ordering.system.service.dataaccess.customer.mapper;
import com.andrei.food.ordering.system.service.entity.Customer;
import com.andrei.food.ordering.system.service.dataaccess.customer.entity.CustomerEntity;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CustomerDataAccessMapperTest {

    @Mock
    private Customer customer;

    @Mock
    private CustomerEntity customerEntity;

    private CustomerDataAccessMapper customerDataAccessMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerDataAccessMapper = new CustomerDataAccessMapper();
    }

    @Test
    void shouldMapCustomerEntityToCustomer() {
        UUID customerId = UUID.randomUUID();
        when(customerEntity.getId()).thenReturn(customerId);

        Customer customer = customerDataAccessMapper.customerEntityToCustomer(customerEntity);

        assertEquals(customerId, customer.getId().getValue());
    }

    @Test
    void shouldMapCustomerToCustomerEntity() {
        UUID customerId = UUID.randomUUID();
        when(customer.getId()).thenReturn(new CustomerId(customerId));
        when(customer.getUserName()).thenReturn("john_doe");
        when(customer.getFirstName()).thenReturn("John");
        when(customer.getLastName()).thenReturn("Doe");

        CustomerEntity customerEntity = customerDataAccessMapper.customerToCustomerEntity(customer);

        assertEquals(customerId, customerEntity.getId());
        assertEquals("john_doe", customerEntity.getUserName());
        assertEquals("John", customerEntity.getFirstName());
        assertEquals("Doe", customerEntity.getLastName());
    }
}
