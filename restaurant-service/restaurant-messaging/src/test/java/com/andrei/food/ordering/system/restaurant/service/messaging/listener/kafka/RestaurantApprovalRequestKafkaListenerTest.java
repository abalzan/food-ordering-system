package com.andrei.food.ordering.system.restaurant.service.messaging.listener.kafka;

import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.andrei.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import com.andrei.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

class RestaurantApprovalRequestKafkaListenerTest {

    @Mock
    private RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener;

    @Mock
    private RestaurantMessagingDataMapper restaurantMessagingDataMapper;

    @InjectMocks
    private RestaurantApprovalRequestKafkaListener restaurantApprovalRequestKafkaListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void receiveProcessesMessagesSuccessfully() {
        RestaurantApprovalRequestAvroModel avroModel = mock(RestaurantApprovalRequestAvroModel.class);
        List<RestaurantApprovalRequestAvroModel> messages = List.of(avroModel);
        List<String> keys = List.of("key1");
        List<Integer> partitions = List.of(0);
        List<Long> offsets = List.of(0L);

        when(restaurantMessagingDataMapper.restaurantApprovalRequestAvroModelToRestaurantApproval(avroModel))
                .thenReturn(mock(RestaurantApprovalRequest.class));

        restaurantApprovalRequestKafkaListener.receive(messages, keys, partitions, offsets);

        verify(restaurantApprovalRequestMessageListener, times(1))
                .approveOrder(any(RestaurantApprovalRequest.class));
    }

    @Test
    void receiveHandlesEmptyMessages() {
        List<RestaurantApprovalRequestAvroModel> messages = List.of();
        List<String> keys = List.of();
        List<Integer> partitions = List.of();
        List<Long> offsets = List.of();

        restaurantApprovalRequestKafkaListener.receive(messages, keys, partitions, offsets);

        verify(restaurantApprovalRequestMessageListener, never())
                .approveOrder(any(RestaurantApprovalRequest.class));
    }
}