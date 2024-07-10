package com.andrei.food.ordering.system.order.service.domain;

import com.andrei.food.ordering.system.domain.OrderDomainService;
import com.andrei.food.ordering.system.domain.OrderDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

}
