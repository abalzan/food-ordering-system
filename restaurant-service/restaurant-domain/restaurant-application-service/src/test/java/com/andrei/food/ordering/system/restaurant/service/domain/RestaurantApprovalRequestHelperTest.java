package com.andrei.food.ordering.system.restaurant.service.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Product;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.andrei.food.ordering.system.restaurant.service.domain.mapper.RestaurantDataMapper;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.andrei.food.ordering.system.restaurant.service.domain.outbox.scheduler.OrderOutboxHelper;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.service.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class RestaurantApprovalRequestHelperTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private OrderApprovalRepository orderApprovalRepository;
    @Mock
    private RestaurantDataMapper restaurantDataMapper;
    @Mock
    private OrderOutboxHelper orderOutboxHelper;
    @Mock
    private RestaurantApprovalResponseMessagePublisher restaurantApprovalResponseMessagePublisher;
    @Mock
    private RestaurantDomainService restaurantDomainService;

    @InjectMocks
    private RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Persists order approval successfully")
    void persistOrderApprovalSuccessfully() {
        UUID productId = UUID.randomUUID();
        OrderDetail orderDetail = OrderDetail.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .products(List.of(Product.builder()
                        .productId(new ProductId(productId))
                        .name("Product")
                        .quantity(10)
                        .price(new Money(BigDecimal.TEN))
                        .build()))
                .orderStatus(OrderStatus.PAID)
                .build();

        RestaurantApprovalRequest restaurantApprovalRequest = RestaurantApprovalRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .orderDetail(orderDetail)
                .build();
        OrderApprovalEvent event = mock(OrderApprovalEvent.class);
        OrderApproval orderApproval = mock(OrderApproval.class);

        when(restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest)).thenReturn(restaurant);
        when(restaurantRepository.findRestaurantInformation(restaurant)).thenReturn(Optional.of(restaurant));
        when(restaurantDomainService.validateOrder(any(), any())).thenReturn(event);
        when(event.getOrderApproval()).thenReturn(orderApproval);
        when(orderApproval.getApprovalStatus()).thenReturn(OrderApprovalStatus.APPROVED);

        restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest);

        verify(orderApprovalRepository).save(any());
        verify(orderOutboxHelper).saveOrderOutboxMessage(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Throws exception when restaurant not found")
    void persistOrderApprovalRestaurantNotFound() {
        RestaurantApprovalRequest restaurantApprovalRequest = RestaurantApprovalRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .build();

        when(restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest)).thenReturn(restaurant);
        when(restaurantRepository.findRestaurantInformation(any())).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest));
    }

    @Test
    @DisplayName("Does not persist order approval when outbox message already processed")
    void doesNotPersistOrderApprovalWhenOutboxMessageAlreadyProcessed() {
        RestaurantApprovalRequest restaurantApprovalRequest = RestaurantApprovalRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();

        when(orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(any(), eq(OutboxStatus.COMPLETED)))
                .thenReturn(Optional.of(mock(OrderOutboxMessage.class)));

        restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest);

        verify(orderApprovalRepository, never()).save(any());
        verify(orderOutboxHelper, never()).saveOrderOutboxMessage(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Publishes outbox message if already processed")
    void publishesOutboxMessageIfAlreadyProcessed() {
        RestaurantApprovalRequest restaurantApprovalRequest = RestaurantApprovalRequest.builder()
                .sagaId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .build();
        OrderOutboxMessage orderOutboxMessage = mock(OrderOutboxMessage.class);

        when(orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(any(), eq(OutboxStatus.COMPLETED)))
                .thenReturn(Optional.of(orderOutboxMessage));

        restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest);

        verify(restaurantApprovalResponseMessagePublisher).publish(any(), any());
    }
}