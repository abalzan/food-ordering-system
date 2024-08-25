package com.andrei.food.ordering.system.restaurant.service.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.andrei.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Product;
import com.andrei.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.andrei.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.andrei.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.andrei.food.ordering.system.restaurant.service.domain.mapper.RestaurantDataMapper;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import com.andrei.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import com.andrei.food.ordering.system.service.valueobject.Money;
import com.andrei.food.ordering.system.service.valueobject.ProductId;
import com.andrei.food.ordering.system.service.valueobject.RestaurantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

class RestaurantApprovalRequestHelperTest {

    @Mock
    private RestaurantDomainService restaurantDomainService;

    @Mock
    private RestaurantDataMapper restaurantDataMapper;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private OrderApprovalRepository orderApprovalRepository;

    @Mock
    private OrderApprovedMessagePublisher orderApprovedMessagePublisher;

    @Mock
    private OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    @InjectMocks
    private RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    UUID productId = UUID.randomUUID();
    @Test
    void persistOrderApprovalSuccessfully() {
        RestaurantApprovalRequest restaurantApprovalRequest = RestaurantApprovalRequest.builder()
                .orderId(UUID.randomUUID().toString())
                .build();
        Restaurant restaurant = Restaurant.builder()
                .active(true)
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .orderDetail(OrderDetail.builder()
                        .products(Collections.singletonList(Product.builder()
                                .productId(new ProductId(productId))
                                .name("product")
                                .price(new Money(new BigDecimal("10.00")))
                                .build()))
                        .build())
                .build();
        OrderApprovalEvent event = mock(OrderApprovalEvent.class);

        when(restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest)).thenReturn(restaurant);
        when(restaurantRepository.findRestaurantInformation(restaurant)).thenReturn(Optional.of(restaurant));
        when(restaurantDomainService.validateOrder(any(), any(), any(), any())).thenReturn(event);

        OrderApprovalEvent result = restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest);

        assertNotNull(result);
        verify(orderApprovalRepository).save(event.getOrderApproval());
    }

    @Test
    void persistOrderApprovalRestaurantNotFound() {
        RestaurantApprovalRequest restaurantApprovalRequest = RestaurantApprovalRequest.builder()
                .orderId(UUID.randomUUID().toString())
                .build();
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.randomUUID()))
                .build();

        when(restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest)).thenReturn(restaurant);
        when(restaurantRepository.findRestaurantInformation(restaurant)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest));
    }
}