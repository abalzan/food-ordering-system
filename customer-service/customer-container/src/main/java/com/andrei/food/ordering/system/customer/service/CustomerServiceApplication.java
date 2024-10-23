package com.andrei.food.ordering.system.customer.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories({"com.andrei.food.ordering.system.customer.service.dataaccess", "com.andrei.food.ordering.system.dataaccess"})
@EntityScan({"com.andrei.food.ordering.system.service.dataaccess", "com.andrei.food.ordering.system.dataaccess", "com.andrei.food.ordering.system.customer.service.dataaccess.entity"})
@SpringBootApplication(scanBasePackages = "com.andrei.food.ordering.system")
public class CustomerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
