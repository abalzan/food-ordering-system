package com.andrei.food.ordering.system.payment.service.dataaccess.payment.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.payment.service.dataaccess.payment.entity.PaymentEntity;
import com.andrei.food.ordering.system.payment.service.dataaccess.payment.mapper.PaymentDataAccessMapper;
import com.andrei.food.ordering.system.payment.service.dataaccess.payment.repository.PaymentJpaRepository;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryImplTest {

    @Mock
    private PaymentJpaRepository paymentJpaRepository;

    @Mock
    private PaymentDataAccessMapper paymentDataAccessMapper;

    @InjectMocks
    private PaymentRepositoryImpl paymentRepositoryImpl;

    @Test
    void savePaymentSuccessfully() {
        Payment payment = Payment.builder().build();
        PaymentEntity paymentEntity = new PaymentEntity();

        when(paymentDataAccessMapper.paymentToPaymentEntity(payment)).thenReturn(paymentEntity);
        when(paymentJpaRepository.save(paymentEntity)).thenReturn(paymentEntity);
        when(paymentDataAccessMapper.paymentEntityToPayment(paymentEntity)).thenReturn(payment);

        Payment result = paymentRepositoryImpl.save(payment);

        assertNotNull(result);
        assertEquals(payment, result);
    }

    @Test
    void findByOrderIdReturnsPayment() {
        OrderId orderId = new OrderId(UUID.randomUUID());
        PaymentEntity paymentEntity = new PaymentEntity();
        Payment payment = Payment.builder().build();

        when(paymentJpaRepository.findByOrderId(orderId.getValue())).thenReturn(Optional.of(paymentEntity));
        when(paymentDataAccessMapper.paymentEntityToPayment(paymentEntity)).thenReturn(payment);

        Optional<Payment> result = paymentRepositoryImpl.findByOrderId(orderId);

        assertTrue(result.isPresent());
        assertEquals(payment, result.get());
    }

    @Test
    void findByOrderIdReturnsEmptyWhenNotFound() {
        OrderId orderId = new OrderId(UUID.randomUUID());

        when(paymentJpaRepository.findByOrderId(orderId.getValue())).thenReturn(Optional.empty());

        Optional<Payment> result = paymentRepositoryImpl.findByOrderId(orderId);

        assertFalse(result.isPresent());
    }
}