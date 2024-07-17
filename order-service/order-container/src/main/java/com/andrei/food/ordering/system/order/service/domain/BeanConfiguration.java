package com.andrei.food.ordering.system.order.service.domain;

import com.andrei.food.ordering.system.service.OrderDomainService;
import com.andrei.food.ordering.system.service.OrderDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

}
