package com.andrei.food.ordering.system.service.messaging.mapper;

import com.andrei.food.ordering.system.service.domain.dto.message.PaymentResponse;
import com.andrei.food.ordering.system.service.domain.dto.message.RestaurantApprovalResponse;
import com.andrei.food.ordering.system.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.andrei.food.ordering.system.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.andrei.food.ordering.system.kafka.order.avro.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

        public PaymentResponse paymentResponseAvroModelToPaymentResponse(PaymentResponseAvroModel paymentResponseAvroModel) {
            return PaymentResponse.builder()
                    .id(paymentResponseAvroModel.getId().toString())
                    .sagaId(paymentResponseAvroModel.getSagaId().toString())
                    .paymentId(paymentResponseAvroModel.getPaymentId().toString())
                    .customerId(paymentResponseAvroModel.getCustomerId().toString())
                    .orderId(paymentResponseAvroModel.getOrderId().toString())
                    .price(paymentResponseAvroModel.getPrice())
                    .createAt(paymentResponseAvroModel.getCreatedAt())
                    .paymentStatus(com.andrei.food.ordering.system.service.valueobject.PaymentStatus.valueOf(paymentResponseAvroModel.getPaymentStatus().name()))
                    .failureMessages(paymentResponseAvroModel.getFailureMessages())
                    .build();
        }

        public RestaurantApprovalResponse approvalResponseAvroModelToApprovalResponse(RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel) {
            return RestaurantApprovalResponse.builder()
                    .id(restaurantApprovalResponseAvroModel.getId().toString())
                    .sagaId(restaurantApprovalResponseAvroModel.getSagaId().toString())
                    .orderId(restaurantApprovalResponseAvroModel.getOrderId().toString())
                    .restaurantId(restaurantApprovalResponseAvroModel.getRestaurantId().toString())
                    .createAt(restaurantApprovalResponseAvroModel.getCreatedAt())
                    .orderApprovalStatus(com.andrei.food.ordering.system.service.valueobject.OrderApprovalStatus.valueOf(restaurantApprovalResponseAvroModel.getOrderApprovalStatus().name()))
                    .failureMessages(restaurantApprovalResponseAvroModel.getFailureMessages())
                    .build();
        }

        public PaymentRequestAvroModel orderPaymentEventToPaymentRequestAvroModel(String sagaId, OrderPaymentEventPayload orderPaymentEventPayload) {
            return PaymentRequestAvroModel.newBuilder()
                    .setId(UUID.randomUUID())
                    .setSagaId(UUID.fromString(sagaId))
                    .setCustomerId(UUID.fromString(orderPaymentEventPayload.getCustomerId()))
                    .setOrderId(UUID.fromString(orderPaymentEventPayload.getOrderId()))
                    .setPrice(orderPaymentEventPayload.getPrice())
                    .setCreatedAt(orderPaymentEventPayload.getCreatedAt().toInstant())
                    .setPaymentOrderStatus(PaymentOrderStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
                    .build();
        }

        public RestaurantApprovalRequestAvroModel orderApprovalEventToRestaurantApprovalRequestAvroModel(String sagaId, OrderApprovalEventPayload orderApprovalEventPayload) {
            return RestaurantApprovalRequestAvroModel.newBuilder()
                    .setId(UUID.randomUUID())
                    .setSagaId(UUID.fromString(sagaId))
                    .setOrderId(UUID.fromString(orderApprovalEventPayload.getOrderId()))
                    .setRestaurantId(UUID.fromString(orderApprovalEventPayload.getRestaurantId()))
                    .setRestaurantOrderStatus(RestaurantOrderStatus.valueOf(orderApprovalEventPayload.getRestaurantOrderStatus()))
                    .setProducts(orderApprovalEventPayload.getProducts().stream().map(product ->
                            com.andrei.food.ordering.system.kafka.order.avro.model.Product.newBuilder()
                                    .setId(product.getId())
                                    .setQuantity(product.getQuantity())
                                    .build()).collect(Collectors.toList()))
                    .setPrice(orderApprovalEventPayload.getPrice())
                    .setCreatedAt(orderApprovalEventPayload.getCreatedAt().toInstant())
                    .build();
        }
}
