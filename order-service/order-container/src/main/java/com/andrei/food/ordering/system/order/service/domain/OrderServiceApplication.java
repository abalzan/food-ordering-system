package com.andrei.food.ordering.system.order.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories({"com.andrei.food.ordering.system.service.dataaccess", "com.andrei.food.ordering.system.dataaccess"})
@EntityScan({"com.andrei.food.ordering.system.service.dataaccess", "com.andrei.food.ordering.system.dataaccess"})
@SpringBootApplication(scanBasePackages = "com.andrei.food.ordering.system")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
