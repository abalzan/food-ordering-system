package com.andrei.food.ordering.system.payment.service.dataaccess.payment.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.domain.valueobject.PaymentId;
import com.andrei.food.ordering.system.payment.service.dataaccess.payment.entity.PaymentEntity;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.service.valueobject.Money;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.service.valueobject.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

class PaymentDataAccessMapperTest {

    private final PaymentDataAccessMapper paymentDataAccessMapper = new PaymentDataAccessMapper();

    @Test
    void paymentToPaymentEntityMapsCorrectly() {
        Payment payment = Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .orderId(new OrderId(UUID.randomUUID()))
                .price(new Money(new BigDecimal("100.00")))
                .paymentStatus(PaymentStatus.COMPLETED)
                .createdAt(ZonedDateTime.now())
                .build();

        PaymentEntity paymentEntity = paymentDataAccessMapper.paymentToPaymentEntity(payment);

        assertNotNull(paymentEntity);
        assertEquals(payment.getId().getValue(), paymentEntity.getId());
        assertEquals(payment.getCustomerId().getValue(), paymentEntity.getCustomerId());
        assertEquals(payment.getOrderId().getValue(), paymentEntity.getOrderId());
        assertEquals(payment.getPrice().getAmount(), paymentEntity.getPrice());
        assertEquals(payment.getPaymentStatus(), paymentEntity.getStatus());
        assertEquals(payment.getCreatedAt(), paymentEntity.getCreatedAt());
    }

    @Test
    void paymentEntityToPaymentMapsCorrectly() {
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .orderId(UUID.randomUUID())
                .price(new BigDecimal("100.00"))
                .status(PaymentStatus.COMPLETED)
                .createdAt(ZonedDateTime.now())
                .build();

        Payment payment = paymentDataAccessMapper.paymentEntityToPayment(paymentEntity);

        assertNotNull(payment);
        assertEquals(paymentEntity.getId(), payment.getId().getValue());
        assertEquals(paymentEntity.getCustomerId(), payment.getCustomerId().getValue());
        assertEquals(paymentEntity.getOrderId(), payment.getOrderId().getValue());
        assertEquals(paymentEntity.getPrice(), payment.getPrice().getAmount());
        assertEquals(paymentEntity.getStatus(), payment.getPaymentStatus());
        assertEquals(paymentEntity.getCreatedAt(), payment.getCreatedAt());
    }

    @Test
    void paymentToPaymentEntityHandlesNullValues() {
        Payment payment = Payment.builder()
                .paymentId(new PaymentId(null))
                .customerId(new CustomerId(null))
                .orderId(new OrderId(null))
                .price(new Money(null))
                .paymentStatus(null)
                .createdAt(null)
                .build();

        PaymentEntity paymentEntity = paymentDataAccessMapper.paymentToPaymentEntity(payment);

        assertNotNull(paymentEntity);
        assertNull(paymentEntity.getId());
        assertNull(paymentEntity.getCustomerId());
        assertNull(paymentEntity.getOrderId());
        assertNull(paymentEntity.getPrice());
        assertNull(paymentEntity.getStatus());
        assertNull(paymentEntity.getCreatedAt());
    }

    @Test
    void paymentEntityToPaymentHandlesNullValues() {
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .id(null)
                .customerId(null)
                .orderId(null)
                .price(null)
                .status(null)
                .createdAt(null)
                .build();

        Payment payment = paymentDataAccessMapper.paymentEntityToPayment(paymentEntity);

        assertNotNull(payment);
        assertNull(payment.getId().getValue());
        assertNull(payment.getCustomerId().getValue());
        assertNull(payment.getOrderId().getValue());
        assertNull(payment.getPrice().getAmount());
        assertNull(payment.getPaymentStatus());
        assertNull(payment.getCreatedAt());
    }
}