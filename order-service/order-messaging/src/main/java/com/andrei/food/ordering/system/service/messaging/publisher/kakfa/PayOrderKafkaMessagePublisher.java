package com.andrei.food.ordering.system.service.messaging.publisher.kakfa;

import com.andrei.food.ordering.system.service.domain.config.OrderServiceConfigData;
import com.andrei.food.ordering.system.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.andrei.food.ordering.system.service.event.OrderPaidEvent;
import com.andrei.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.andrei.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.andrei.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final OrderKafkaMessageHelper orderKafkaMessageHelper;

    public PayOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper, OrderServiceConfigData orderServiceConfigData, KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer, OrderKafkaMessageHelper orderKafkaMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderKafkaMessageHelper = orderKafkaMessageHelper;
    }


    @Override
    public void publish(OrderPaidEvent domainEvent) {

        try {
            String orderId = domainEvent.getOrder().getId().getValue().toString();
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel = orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(domainEvent);
            kafkaProducer.send(orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                    orderId,
                    restaurantApprovalRequestAvroModel,
                    orderKafkaMessageHelper.getKafkaCallback(orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                            restaurantApprovalRequestAvroModel,
                            orderId,
                            "RestaurantApprovalRequestAvroModel"));

            log.info("RestaurantApprovalRequestAvroModel sent to kafka for order id: {}", restaurantApprovalRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending message to kafka topic: {} with message: {}", orderServiceConfigData.getRestaurantApprovalRequestTopicName(), domainEvent, e);
        }
    }
}
