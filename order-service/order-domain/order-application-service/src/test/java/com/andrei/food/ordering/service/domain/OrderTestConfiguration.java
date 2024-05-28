package com.andrei.food.ordering.service.domain;

import com.andrei.food.ordering.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.andrei.food.ordering.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.andrei.food.ordering.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.andrei.food.ordering.service.domain.ports.output.repository.CustomerRepository;
import com.andrei.food.ordering.service.domain.ports.output.repository.OrderRepository;
import com.andrei.food.ordering.service.domain.ports.output.repository.RestaurantRepository;
import com.andrei.food.ordering.system.domain.OrderDomainService;
import com.andrei.food.ordering.system.domain.OrderDomainServiceImpl;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.andrei.food.ordering.service")
public class OrderTestConfiguration {

    @Bean
    public OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher() {
        return Mockito.mock(OrderCreatedPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher() {
        return Mockito.mock(OrderCancelledPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher() {
        return Mockito.mock(OrderPaidRestaurantRequestMessagePublisher.class);
    }

    @Bean
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    public RestaurantRepository restaurantRepository() {
        return Mockito.mock(RestaurantRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}
