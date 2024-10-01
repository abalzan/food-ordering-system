package com.andrei.food.ordering.system.service.dataaccess.outbox.restaurantapproval.adapter;

import com.andrei.food.ordering.system.SagaStatus;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.dataaccess.outbox.restaurantapproval.entity.ApprovalOutboxEntity;
import com.andrei.food.ordering.system.service.dataaccess.outbox.restaurantapproval.exception.ApprovalOutboxNotFoundException;
import com.andrei.food.ordering.system.service.dataaccess.outbox.restaurantapproval.mapper.ApprovalOutboxDataAccessMapper;
import com.andrei.food.ordering.system.service.dataaccess.outbox.restaurantapproval.repository.ApprovalOutboxJpaRepository;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApprovalOutboxRepositoryImplTest {

    @Mock
    private ApprovalOutboxJpaRepository approvalOutboxJpaRepository;

    @Mock
    private ApprovalOutboxDataAccessMapper approvalOutboxDataAccessMapper;

    @InjectMocks
    private ApprovalOutboxRepositoryImpl approvalOutboxRepositoryImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveReturnsSavedMessage() {
        OrderApprovalOutboxMessage message = OrderApprovalOutboxMessage.builder().id(UUID.randomUUID()).build();
        when(approvalOutboxDataAccessMapper.orderCreatedOutboxMessageToOutboxEntity(any(OrderApprovalOutboxMessage.class)))
                .thenReturn(new ApprovalOutboxEntity());
        when(approvalOutboxJpaRepository.save(any(ApprovalOutboxEntity.class)))
                .thenReturn(new ApprovalOutboxEntity());
        when(approvalOutboxDataAccessMapper.approvalOutboxEntityToOrderApprovalOutboxMessage(any(ApprovalOutboxEntity.class)))
                .thenReturn(message);

        OrderApprovalOutboxMessage result = approvalOutboxRepositoryImpl.save(message);

        assertEquals(message, result);
    }

    @Test
    void findByTypeAndOutboxStatusAndSagaStatusReturnsMessages() {
        List<ApprovalOutboxEntity> entities = List.of(new ApprovalOutboxEntity());
        when(approvalOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(anyString(), any(OutboxStatus.class), anyList()))
                .thenReturn(Optional.of(entities));
        when(approvalOutboxDataAccessMapper.approvalOutboxEntityToOrderApprovalOutboxMessage(any(ApprovalOutboxEntity.class)))
                .thenReturn(OrderApprovalOutboxMessage.builder().id(UUID.randomUUID()).build());

        Optional<List<OrderApprovalOutboxMessage>> result = approvalOutboxRepositoryImpl.findByTypeAndOutboxStatusAndSagaStatus("type", OutboxStatus.STARTED, SagaStatus.STARTED);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    @Test
    void findByTypeAndOutboxStatusAndSagaStatusThrowsExceptionWhenNotFound() {
        when(approvalOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(anyString(), any(OutboxStatus.class), anyList()))
                .thenReturn(Optional.empty());

        assertThrows(ApprovalOutboxNotFoundException.class, () -> approvalOutboxRepositoryImpl.findByTypeAndOutboxStatusAndSagaStatus("type", OutboxStatus.STARTED, SagaStatus.STARTED));
    }

    @Test
    void findByTypeAndSagaIdAndSagaStatusReturnsMessage() {
        ApprovalOutboxEntity entity = new ApprovalOutboxEntity();
        when(approvalOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(anyString(), any(UUID.class), anyList()))
                .thenReturn(Optional.of(entity));
        when(approvalOutboxDataAccessMapper.approvalOutboxEntityToOrderApprovalOutboxMessage(any(ApprovalOutboxEntity.class)))
                .thenReturn(OrderApprovalOutboxMessage.builder().id(UUID.randomUUID()).build());

        Optional<OrderApprovalOutboxMessage> result = approvalOutboxRepositoryImpl.findByTypeAndSagaIdAndSagaStatus("type", UUID.randomUUID(), SagaStatus.STARTED);

        assertTrue(result.isPresent());
    }

    @Test
    void findByTypeAndSagaIdAndSagaStatusReturnsEmptyWhenNotFound() {
        when(approvalOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(anyString(), any(UUID.class), anyList()))
                .thenReturn(Optional.empty());

        Optional<OrderApprovalOutboxMessage> result = approvalOutboxRepositoryImpl.findByTypeAndSagaIdAndSagaStatus("type", UUID.randomUUID(), SagaStatus.STARTED);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteByTypeAndOutboxStatusAndSagaStatusDeletesMessages() {
        doNothing().when(approvalOutboxJpaRepository).deleteByTypeAndOutboxStatusAndSagaStatusIn(anyString(), any(OutboxStatus.class), anyList());

        approvalOutboxRepositoryImpl.deleteByTypeAndOutboxStatusAndSagaStatus("type", OutboxStatus.STARTED, SagaStatus.STARTED);

        verify(approvalOutboxJpaRepository, times(1)).deleteByTypeAndOutboxStatusAndSagaStatusIn(anyString(), any(OutboxStatus.class), anyList());
    }
}