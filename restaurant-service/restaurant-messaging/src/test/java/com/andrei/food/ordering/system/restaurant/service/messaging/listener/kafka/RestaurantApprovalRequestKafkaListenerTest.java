package com.andrei.food.ordering.system.restaurant.service.messaging.listener.kafka;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.andrei.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.andrei.food.ordering.system.restaurant.service.domain.exception.RestaurantApplicationServiceException;
import com.andrei.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import com.andrei.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;

import java.sql.SQLException;
import java.util.List;

class RestaurantApprovalRequestKafkaListenerTest {

    @Mock
    private RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener;

    @Mock
    private RestaurantMessagingDataMapper restaurantMessagingDataMapper;

    @InjectMocks
    private RestaurantApprovalRequestKafkaListener restaurantApprovalRequestKafkaListener;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logCaptor = LogCaptor.forClass(RestaurantApprovalRequestKafkaListener.class);
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

    @DisplayName("Logs error and does not throw exception for unique constraint violation")
    @Test
    void receiveLogsErrorForUniqueConstraintViolation() {
        RestaurantApprovalRequestAvroModel avroModel = mock(RestaurantApprovalRequestAvroModel.class);
        RestaurantApprovalRequest restaurantApprovalRequest = mock(RestaurantApprovalRequest.class);
        List<RestaurantApprovalRequestAvroModel> messages = List.of(avroModel);
        List<String> keys = List.of("key1");
        List<Integer> partitions = List.of(0);
        List<Long> offsets = List.of(0L);

        when(restaurantMessagingDataMapper.restaurantApprovalRequestAvroModelToRestaurantApproval(avroModel))
                .thenReturn(restaurantApprovalRequest);

        SQLException sqlException = mock(SQLException.class);
        when(sqlException.getSQLState()).thenReturn(PSQLState.UNIQUE_VIOLATION.getState());
        DataAccessException dataAccessException = mock(DataAccessException.class);
        when(dataAccessException.getRootCause()).thenReturn(sqlException);

        doThrow(dataAccessException).when(restaurantApprovalRequestMessageListener)
                .approveOrder(any(RestaurantApprovalRequest.class));

        restaurantApprovalRequestKafkaListener.receive(messages, keys, partitions, offsets);

        verify(restaurantApprovalRequestMessageListener, times(1))
                .approveOrder(any(RestaurantApprovalRequest.class));
        assertTrue(logCaptor.getErrorLogs().stream().anyMatch(log -> log.contains("Caught unique constraint exception")));
    }

    @DisplayName("Logs error and does not throw exception for RestaurantNotFoundException")
    @Test
    void receiveLogsErrorForRestaurantNotFoundException() {
        RestaurantApprovalRequestAvroModel avroModel = mock(RestaurantApprovalRequestAvroModel.class);
        RestaurantApprovalRequest restaurantApprovalRequest = mock(RestaurantApprovalRequest.class);
        List<RestaurantApprovalRequestAvroModel> messages = List.of(avroModel);
        List<String> keys = List.of("key1");
        List<Integer> partitions = List.of(0);
        List<Long> offsets = List.of(0L);

        when(restaurantMessagingDataMapper.restaurantApprovalRequestAvroModelToRestaurantApproval(avroModel))
                .thenReturn(restaurantApprovalRequest);

        doThrow(RestaurantNotFoundException.class).when(restaurantApprovalRequestMessageListener)
                .approveOrder(any(RestaurantApprovalRequest.class));

        restaurantApprovalRequestKafkaListener.receive(messages, keys, partitions, offsets);

        verify(restaurantApprovalRequestMessageListener, times(1))
                .approveOrder(any(RestaurantApprovalRequest.class));
        assertTrue(logCaptor.getErrorLogs().stream().anyMatch(log -> log.contains("No restaurant found")));
    }

    @DisplayName("Throws RestaurantApplicationServiceException for other DataAccessException")
    @Test
    void receiveThrowsExceptionForOtherDataAccessException() {
        RestaurantApprovalRequestAvroModel avroModel = mock(RestaurantApprovalRequestAvroModel.class);
        RestaurantApprovalRequest restaurantApprovalRequest = mock(RestaurantApprovalRequest.class);
        List<RestaurantApprovalRequestAvroModel> messages = List.of(avroModel);
        List<String> keys = List.of("key1");
        List<Integer> partitions = List.of(0);
        List<Long> offsets = List.of(0L);

        when(restaurantMessagingDataMapper.restaurantApprovalRequestAvroModelToRestaurantApproval(avroModel))
                .thenReturn(restaurantApprovalRequest);

        DataAccessException dataAccessException = mock(DataAccessException.class);
        doThrow(dataAccessException).when(restaurantApprovalRequestMessageListener)
                .approveOrder(any(RestaurantApprovalRequest.class));

        Assertions.assertThrows(RestaurantApplicationServiceException.class, () -> {
            restaurantApprovalRequestKafkaListener.receive(messages, keys, partitions, offsets);
        });
    }
}