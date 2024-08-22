package com.andrei.food.ordering.system.payment.service.domain;

import com.andrei.food.ordering.system.domain.PaymentDomainService;
import com.andrei.food.ordering.system.domain.PaymentDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfigurarion {

    @Bean
    public PaymentDomainService paymentDomainService() {
        return new PaymentDomainServiceImpl();
    }
}
