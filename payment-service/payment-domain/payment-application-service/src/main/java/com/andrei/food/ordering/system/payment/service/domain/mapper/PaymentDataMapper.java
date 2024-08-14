package com.andrei.food.ordering.system.payment.service.domain.mapper;

import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.service.valueobject.Money;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentDataMapper {

    public Payment paymentRequestModelToPayment(PaymentRequest paymentRequest) {
        return Payment.builder()
                .orderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
                .customerId(new CustomerId(UUID.fromString(paymentRequest.getCustomerId())))
                .price(new Money(paymentRequest.getPrice()))
                .build();
    }
}
