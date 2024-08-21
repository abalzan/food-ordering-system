package com.andrei.food.ordering.system.payment.service.messaging.listener.kafka;

import com.andrei.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import com.andrei.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.andrei.food.ordering.system.kafka.order.avro.model.PaymentOrderStatus;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

class PaymentRequestKafkaListenerTest {

    @Mock
    private PaymentRequestMessageListener paymentRequestMessageListener;

    @Mock
    private PaymentMessagingDataMapper paymentMessagingDataMapper;

    @InjectMocks
    private PaymentRequestKafkaListener paymentRequestKafkaListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentRequestWithPendingStatusIsProcessedSuccessfully() {
        PaymentRequestAvroModel paymentRequestAvroModel = mock(PaymentRequestAvroModel.class);
        when(paymentRequestAvroModel.getPaymentOrderStatus()).thenReturn(PaymentOrderStatus.PENDING);
        when(paymentRequestAvroModel.getOrderId()).thenReturn(UUID.randomUUID());

        List<PaymentRequestAvroModel> messages = Collections.singletonList(paymentRequestAvroModel);
        List<String> keys = Collections.singletonList("key");
        List<Integer> partitions = Collections.singletonList(0);
        List<Long> offsets = Collections.singletonList(0L);

        paymentRequestKafkaListener.receive(messages, keys, partitions, offsets);

        verify(paymentRequestMessageListener).completePayment(any());
    }

    @Test
    void paymentRequestWithCancelledStatusIsProcessedSuccessfully() {
        PaymentRequestAvroModel paymentRequestAvroModel = mock(PaymentRequestAvroModel.class);
        when(paymentRequestAvroModel.getPaymentOrderStatus()).thenReturn(PaymentOrderStatus.CANCELLED);
        when(paymentRequestAvroModel.getOrderId()).thenReturn(UUID.randomUUID());

        List<PaymentRequestAvroModel> messages = Collections.singletonList(paymentRequestAvroModel);
        List<String> keys = Collections.singletonList("key");
        List<Integer> partitions = Collections.singletonList(0);
        List<Long> offsets = Collections.singletonList(0L);

        paymentRequestKafkaListener.receive(messages, keys, partitions, offsets);

        verify(paymentRequestMessageListener).cancelPayment(any());
    }

    @Test
    void multiplePaymentRequestsAreProcessedSuccessfully() {
        PaymentRequestAvroModel pendingRequest = mock(PaymentRequestAvroModel.class);
        when(pendingRequest.getPaymentOrderStatus()).thenReturn(PaymentOrderStatus.PENDING);
        when(pendingRequest.getOrderId()).thenReturn(UUID.randomUUID());

        PaymentRequestAvroModel cancelledRequest = mock(PaymentRequestAvroModel.class);
        when(cancelledRequest.getPaymentOrderStatus()).thenReturn(PaymentOrderStatus.CANCELLED);
        when(cancelledRequest.getOrderId()).thenReturn(UUID.randomUUID());

        List<PaymentRequestAvroModel> messages = Arrays.asList(pendingRequest, cancelledRequest);
        List<String> keys = Arrays.asList("key1", "key2");
        List<Integer> partitions = Arrays.asList(0, 1);
        List<Long> offsets = Arrays.asList(0L, 1L);

        paymentRequestKafkaListener.receive(messages, keys, partitions, offsets);

        verify(paymentRequestMessageListener).completePayment(any());
        verify(paymentRequestMessageListener).cancelPayment(any());
    }
}