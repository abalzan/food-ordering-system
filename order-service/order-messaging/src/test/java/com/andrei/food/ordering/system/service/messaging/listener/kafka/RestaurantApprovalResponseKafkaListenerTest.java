package com.andrei.food.ordering.system.service.messaging.listener.kafka;

import com.andrei.food.ordering.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import com.andrei.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class RestaurantApprovalResponseKafkaListenerTest {

    @Mock
    private RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
    @Mock
    private OrderMessagingDataMapper orderMessagingDataMapper;

    @InjectMocks
    private RestaurantApprovalResponseKafkaListener restaurantApprovalResponseKafkaListener;

    private final UUID id = UUID.randomUUID();
    private final UUID sagaId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final UUID restaurantId = UUID.randomUUID();
    private final Instant createdAt = Instant.now();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should process approved restaurant approval responses correctly")
    void shouldProcessApprovedRestaurantApprovalResponsesCorrectly() {
        RestaurantApprovalResponseAvroModel approvedResponse = new RestaurantApprovalResponseAvroModel(id, sagaId, orderId, restaurantId, createdAt, OrderApprovalStatus.APPROVED, new ArrayList<>(Collections.emptyList()));
        List<RestaurantApprovalResponseAvroModel> messages = Collections.singletonList(approvedResponse);

        restaurantApprovalResponseKafkaListener.receive(messages, Collections.singletonList("key"), Collections.singletonList(0), Collections.singletonList(0L));

        verify(restaurantApprovalResponseMessageListener, times(1)).orderApproved(any());
        verify(restaurantApprovalResponseMessageListener, never()).orderRejected(any());
    }

    @Test
    @DisplayName("Should process rejected restaurant approval responses correctly")
    void shouldProcessRejectedRestaurantApprovalResponsesCorrectly() {
        RestaurantApprovalResponseAvroModel rejectedResponse = new RestaurantApprovalResponseAvroModel(id, sagaId, orderId, restaurantId, createdAt, OrderApprovalStatus.REJECTED, new ArrayList<>(Collections.emptyList()));
        List<RestaurantApprovalResponseAvroModel> messages = Collections.singletonList(rejectedResponse);

        restaurantApprovalResponseKafkaListener.receive(messages, Collections.singletonList("key"), Collections.singletonList(0), Collections.singletonList(0L));

        verify(restaurantApprovalResponseMessageListener, never()).orderApproved(any());
        verify(restaurantApprovalResponseMessageListener, times(1)).orderRejected(any());
    }

    @Test
    @DisplayName("Should handle empty list of messages gracefully")
    void shouldHandleEmptyListOfMessagesGracefully() {
        List<RestaurantApprovalResponseAvroModel> messages = Collections.emptyList();

        restaurantApprovalResponseKafkaListener.receive(messages, Collections.singletonList("key"), Collections.singletonList(0), Collections.singletonList(0L));

        verify(restaurantApprovalResponseMessageListener, never()).orderApproved(any());
        verify(restaurantApprovalResponseMessageListener, never()).orderRejected(any());
    }
}