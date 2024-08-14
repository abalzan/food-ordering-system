package com.andrei.food.ordering.system.payment.service.domain.ports.output.repository;

import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.service.valueobject.OrderId;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByOrderId(OrderId orderId);
}
