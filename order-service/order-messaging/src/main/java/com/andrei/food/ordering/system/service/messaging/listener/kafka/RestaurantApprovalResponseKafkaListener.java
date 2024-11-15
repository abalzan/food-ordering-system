package com.andrei.food.ordering.system.service.messaging.listener.kafka;

import com.andrei.food.ordering.system.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import com.andrei.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.andrei.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.andrei.food.ordering.system.service.exception.OrderNotFoundException;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {
    private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(@Payload List<RestaurantApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("{} number of restaurant approval responses received with keys {} from partitions {} with offsets {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());
        messages.forEach(restaurantApprovalResponseAvroModel -> {
            try {
                log.info("Restaurant approval response with id {} is completed for order id {}", restaurantApprovalResponseAvroModel.getOrderApprovalStatus(), restaurantApprovalResponseAvroModel.getOrderId());
                if(restaurantApprovalResponseAvroModel.getOrderApprovalStatus().equals(OrderApprovalStatus.APPROVED)){
                    log.info("Restaurant approval response with id {} is completed for order id {}", restaurantApprovalResponseAvroModel.getOrderApprovalStatus(), restaurantApprovalResponseAvroModel.getOrderId());
                    restaurantApprovalResponseMessageListener.orderApproved(orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(restaurantApprovalResponseAvroModel));
                } else if(restaurantApprovalResponseAvroModel.getOrderApprovalStatus().equals(OrderApprovalStatus.REJECTED)){
                    log.info("Restaurant approval response with id {} is failed for order id {}", restaurantApprovalResponseAvroModel.getOrderApprovalStatus(), restaurantApprovalResponseAvroModel.getOrderId());
                    restaurantApprovalResponseMessageListener.orderRejected(orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(restaurantApprovalResponseAvroModel));
                }
            } catch (OptimisticLockingFailureException e) {
                // This means another thread has updated the order status, so we can ignore this exception
                log.error("OptimisticLockingFailureException occurred while processing restaurant approval response with id {} for order id {}", restaurantApprovalResponseAvroModel.getOrderApprovalStatus(), restaurantApprovalResponseAvroModel.getOrderId(), e);
            } catch (OrderNotFoundException e) {
                log.error("OrderNotFoundException occurred while processing restaurant approval response with id {} for order id {}", restaurantApprovalResponseAvroModel.getOrderApprovalStatus(), restaurantApprovalResponseAvroModel.getOrderId(), e);
            }
        });
    }
}
