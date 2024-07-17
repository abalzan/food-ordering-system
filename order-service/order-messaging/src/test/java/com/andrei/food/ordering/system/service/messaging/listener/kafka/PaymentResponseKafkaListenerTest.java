package com.andrei.food.ordering.system.service.messaging.listener.kafka;

import com.andrei.food.ordering.system.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class PaymentResponseKafkaListenerTest {

    @Mock
    private PaymentResponseMessageListener paymentResponseMessageListener;
    @Mock
    private OrderMessagingDataMapper orderMessagingDataMapper;

    @InjectMocks
    private PaymentResponseKafkaListener paymentResponseKafkaListener;

    private PaymentResponseAvroModel completedPaymentResponse;
    private PaymentResponseAvroModel failedPaymentResponse;
    private PaymentResponseAvroModel cancelledPaymentResponse;

    private final UUID id = UUID.randomUUID();
    private final UUID sagaId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final UUID paymentId = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();
    private final Instant createdAt = Instant.now();
    private final BigDecimal price = new BigDecimal(10);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        completedPaymentResponse = new PaymentResponseAvroModel(id, sagaId, paymentId, customerId, orderId, price, createdAt, PaymentStatus.COMPLETED, new ArrayList<>());
        failedPaymentResponse = new PaymentResponseAvroModel(id, sagaId, paymentId, customerId, orderId, price, createdAt, PaymentStatus.FAILED, new ArrayList<>());
        cancelledPaymentResponse = new PaymentResponseAvroModel(id, sagaId, paymentId, customerId, orderId, price, createdAt, PaymentStatus.CANCELLED, new ArrayList<>());
    }

    @Test
    @DisplayName("Should process completed payment responses correctly")
    void shouldProcessCompletedPaymentResponsesCorrectly() {
        List<PaymentResponseAvroModel> messages = Collections.singletonList(completedPaymentResponse);

        paymentResponseKafkaListener.receive(messages, Collections.singletonList("key"), Collections.singletonList(0), Collections.singletonList(0L));

        verify(paymentResponseMessageListener, times(1)).paymentCompleted(any());
        verify(paymentResponseMessageListener, never()).paymentCancelled(any());
    }

    @Test
    @DisplayName("Should process failed payment responses correctly")
    void shouldProcessFailedPaymentResponsesCorrectly() {
        List<PaymentResponseAvroModel> messages = Collections.singletonList(failedPaymentResponse);

        paymentResponseKafkaListener.receive(messages, Collections.singletonList("key"), Collections.singletonList(0), Collections.singletonList(0L));

        verify(paymentResponseMessageListener, never()).paymentCompleted(any());
        verify(paymentResponseMessageListener, times(1)).paymentCancelled(any());
    }

    @Test
    @DisplayName("Should process cancelled payment responses correctly")
    void shouldProcessCancelledPaymentResponsesCorrectly() {
        List<PaymentResponseAvroModel> messages = Collections.singletonList(cancelledPaymentResponse);

        paymentResponseKafkaListener.receive(messages, Collections.singletonList("key"), Collections.singletonList(0), Collections.singletonList(0L));

        verify(paymentResponseMessageListener, never()).paymentCompleted(any());
        verify(paymentResponseMessageListener, times(1)).paymentCancelled(any());
    }
}