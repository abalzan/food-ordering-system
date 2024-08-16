package com.andrei.food.ordering.system.payment.service.dataaccess.payment.adapter;


import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.payment.service.dataaccess.payment.mapper.PaymentDataAccessMapper;
import com.andrei.food.ordering.system.payment.service.dataaccess.payment.repository.PaymentJpaRepository;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentDataAccessMapper paymentDataAccessMapper;

    @Override
    public Payment save(Payment payment) {
        return paymentDataAccessMapper
                .paymentEntityToPayment(paymentJpaRepository
                        .save(paymentDataAccessMapper.paymentToPaymentEntity(payment)));
    }

    @Override
    public Optional<Payment> findByOrderId(OrderId orderId) {
        return paymentJpaRepository.findByOrderId(orderId.getValue())
                .map(paymentDataAccessMapper::paymentEntityToPayment);
    }
}
