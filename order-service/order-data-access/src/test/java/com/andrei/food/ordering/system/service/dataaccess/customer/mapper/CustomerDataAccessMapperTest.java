package com.andrei.food.ordering.system.service.dataaccess.customer.mapper;
import com.andrei.food.ordering.system.domain.entity.Customer;
import com.andrei.food.ordering.system.service.dataaccess.customer.entity.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CustomerDataAccessMapperTest {

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
}
