package com.andrei.food.ordering.system.payment.service.domain;

import com.andrei.food.ordering.system.order.SagaConstants;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.payment.service.dataaccess.outbox.entity.OrderOutboxEntity;
import com.andrei.food.ordering.system.payment.service.dataaccess.outbox.repository.OrderOutboxJpaRepository;
import com.andrei.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.andrei.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import com.andrei.food.ordering.system.service.valueobject.PaymentOrderStatus;
import com.andrei.food.ordering.system.service.valueobject.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@SpringBootTest(classes = PaymentServiceApplication.class)
public class PaymentRequestMessageListenerTest {

    @Autowired
    private PaymentRequestMessageListener paymentRequestMessageListener;

    @Autowired
    private OrderOutboxJpaRepository orderOutboxJpaRepository;

    private final static String CUSTOMER_ID = "d215b5f8-0249-4dc5-89a3-51fd148cfb41";
    private final static BigDecimal PRICE = new BigDecimal(100);

    @Test
    void testDoublePayment() {
        String sagaId = UUID.randomUUID().toString();
        paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));

        // should throw DataAccessException when trying to complete payment for the second time
        Assertions.assertThrows(DataAccessException.class, () -> paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId)));
        
        assertOrderOutbox(sagaId);
    
    }

    private void assertOrderOutbox(String sagaId) {
        Optional<OrderOutboxEntity> orderOutboxEntity = orderOutboxJpaRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
                SagaConstants.ORDER_SAGA_NAME,
                UUID.fromString(sagaId),
                PaymentStatus.COMPLETED,
                OutboxStatus.STARTED);

        Assertions.assertTrue(orderOutboxEntity.isPresent());
        Assertions.assertEquals(orderOutboxEntity.get().getSagaId().toString(), sagaId);

    }


    private PaymentRequest getPaymentRequest(String sagaId) {
        return PaymentRequest.builder()
                .id(UUID.randomUUID().toString())
                .customerId(CUSTOMER_ID)
                .orderId(UUID.randomUUID().toString())
                .sagaId(sagaId)
                .paymentOrderStatus(PaymentOrderStatus.PENDING)
                .price(PRICE)
                .createdAt(Instant.now())
                .build();
    }
}
