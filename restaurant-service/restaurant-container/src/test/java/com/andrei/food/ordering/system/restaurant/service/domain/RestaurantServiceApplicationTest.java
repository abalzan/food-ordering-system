package com.andrei.food.ordering.system.restaurant.service.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RestaurantServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void applicationContextLoadsSuccessfully() {
        assertNotNull(applicationContext);
    }

    @Test
    void mainMethodRunsApplication() {
        assertDoesNotThrow(() -> RestaurantServiceApplication.main(new String[] {}));
    }
}