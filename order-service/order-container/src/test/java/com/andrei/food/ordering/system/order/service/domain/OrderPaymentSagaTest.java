package com.andrei.food.ordering.system.order.service.domain;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.order.SagaConstants;
import com.andrei.food.ordering.system.service.dataaccess.outbox.payment.entity.PaymentOutboxEntity;
import com.andrei.food.ordering.system.service.dataaccess.outbox.payment.repository.PaymentOutboxJpaRepository;
import com.andrei.food.ordering.system.service.domain.OrderPaymentSaga;
import com.andrei.food.ordering.system.service.domain.dto.message.PaymentResponse;
import com.andrei.food.ordering.system.service.valueobject.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.log.LogCaptor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = OrderServiceApplication.class)
@Sql(value = "classpath:sql/OrderPaymentSagaTestSetUp.sql")
@Sql(value = "classpath:sql/OrderPaymentSagaTestCleanUp.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderPaymentSagaTest {

    @Autowired
    private OrderPaymentSaga orderPaymentSaga;

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    private final UUID SAGA_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
    private final UUID ORDER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb17");
    private final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    private final UUID PAYMENT_ID = UUID.randomUUID();
    private final BigDecimal PRICE = new BigDecimal("100");

    @Test
    void testDoublePayment() {
        LogCaptor logCaptor = LogCaptor.forClass(OrderPaymentSaga.class);
        orderPaymentSaga.process(getPaymentResponse());
        Assertions.assertThat(logCaptor.getInfoLogs())
                .contains("Payment for order with id "+ORDER_ID+" was successful")
                .doesNotContain("An outbox message with sagaId "+SAGA_ID+" is already processed");

        orderPaymentSaga.process(getPaymentResponse());
        Assertions.assertThat(logCaptor.getInfoLogs())
                .contains("An outbox message with sagaId "+SAGA_ID+" is already processed");
    }

    @Test
    void testDoublePaymentWithThreads() throws InterruptedException {
        Thread thread = new Thread(() -> orderPaymentSaga.process(getPaymentResponse()));
        Thread thread1 = new Thread(() -> orderPaymentSaga.process(getPaymentResponse()));

        thread.start();
        thread1.start();

        thread.join();
        thread1.join();

        assertPaymentOutbox();
    }

    @Test
    void testDoublePaymentWithCountDownLatch() throws InterruptedException {
        LogCaptor logCaptor = LogCaptor.forClass(OrderPaymentSagaTest.class);

        CountDownLatch countDownLatch = new CountDownLatch(2);

        Thread thread = new Thread(() -> {
            try {
                orderPaymentSaga.process(getPaymentResponse());
            } catch (OptimisticLockingFailureException e) {
                log.error("OptimisticLockingFailureException occurred", e);
            } finally {
                countDownLatch.countDown();
            }
        });

        Thread thread1 = new Thread(() -> {
            try {
                orderPaymentSaga.process(getPaymentResponse());
            } catch (OptimisticLockingFailureException e) {
                log.error("OptimisticLockingFailureException occurred", e);
            } finally {
                countDownLatch.countDown();
            }
        });

        thread.start();
        thread1.start();

        countDownLatch.await();

        assertPaymentOutbox();
        // OptimisticLockingFailureException should be logged once, because the second thread will try to update the same record
        logCaptor.getErrorLogs().containsAll(Collections.singleton("OptimisticLockingFailureException occurred"));
    }

    private void assertPaymentOutbox() {
        Optional<PaymentOutboxEntity> paymentOutboxEntity = paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(SagaConstants.ORDER_SAGA_NAME, SAGA_ID,
                List.of(SagaStatus.PROCESSING));

        assertTrue(paymentOutboxEntity.isPresent());
    }

    private PaymentResponse getPaymentResponse() {
        return PaymentResponse.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(SAGA_ID.toString())
                .paymentStatus(PaymentStatus.COMPLETED)
                .orderId(ORDER_ID.toString())
                .customerId(CUSTOMER_ID.toString())
                .paymentId(PAYMENT_ID.toString())
                .price(PRICE)
                .createAt(Instant.now())
                .failureMessages(new ArrayList<>())
                .build();
    }

}
