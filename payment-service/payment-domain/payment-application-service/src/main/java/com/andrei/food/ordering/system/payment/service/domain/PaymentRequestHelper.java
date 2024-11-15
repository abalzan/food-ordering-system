package com.andrei.food.ordering.system.payment.service.domain;

import com.andrei.food.ordering.system.domain.PaymentDomainService;
import com.andrei.food.ordering.system.domain.entity.CreditEntry;
import com.andrei.food.ordering.system.domain.entity.CreditHistory;
import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.domain.event.PaymentEvent;
import com.andrei.food.ordering.system.domain.exception.PaymentNotFoundException;
import com.andrei.food.ordering.system.outbox.OutboxStatus;
import com.andrei.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.andrei.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.andrei.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper;
import com.andrei.food.ordering.system.payment.service.domain.outbox.scheduler.OrderOutboxHelper;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.andrei.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import com.andrei.food.ordering.system.service.valueobject.CustomerId;
import com.andrei.food.ordering.system.service.valueobject.OrderId;
import com.andrei.food.ordering.system.service.valueobject.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentRequestHelper {
    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final OrderOutboxHelper orderOutboxHelper;
    private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    @Transactional
    public void persistPayment(PaymentRequest paymentRequest) {

        if(publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.COMPLETED)) {
            log.info("Outbox Message with saga id {} and payment status {} already saved to the database", paymentRequest.getSagaId(), PaymentStatus.COMPLETED);
            return;
        }

        log.info("Received Payment complete event for order id {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories,
                failureMessages);
        persistDBObject(payment, failureMessages, creditEntry, creditHistories);

        orderOutboxHelper.saveOrderOutboxMessage(paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(), OutboxStatus.STARTED, UUID.fromString(paymentRequest.getSagaId()));

    }

    @Transactional
    public void persistCancelPayment(PaymentRequest paymentRequest) {
        if(publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.CANCELLED)) {
            log.info("Outbox Message with saga id {} and payment status {} already saved to the database", paymentRequest.getSagaId(), PaymentStatus.COMPLETED);
            return;
        }
       log.info("Received Payment cancel event for order id {}", paymentRequest.getOrderId());
        Payment payment = paymentRepository.findByOrderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
                .orElseThrow(() -> {
                    log.error("Could not found the payment for order id {}", paymentRequest.getOrderId());
                    return new PaymentNotFoundException("Could not find the payment for order id " + paymentRequest.getOrderId());
                });
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(payment, creditEntry, creditHistories, failureMessages);
        persistDBObject(payment, failureMessages, creditEntry, creditHistories);

        orderOutboxHelper.saveOrderOutboxMessage(paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(), OutboxStatus.STARTED, UUID.fromString(paymentRequest.getSagaId()));

    }


    private CreditEntry getCreditEntry(CustomerId customerId) {
        return creditEntryRepository.findByCustomerId(customerId).orElseThrow(() -> {
            log.error("Could not found the credit entry for customer id {}", customerId.getValue());
            return new PaymentApplicationServiceException("Could not find the credit entry for customer id " + customerId.getValue());
        });
    }

    private List<CreditHistory> getCreditHistories(CustomerId customerId) {
        return creditHistoryRepository.findByCustomerId(customerId).orElseThrow(() -> {
            log.error("Could not found the credit history for customer id {}", customerId.getValue());
            return new PaymentApplicationServiceException("Could not find the credit history for customer id " + customerId.getValue());
        });
    }

    private void persistDBObject(Payment payment, List<String> failureMessages, CreditEntry creditEntry, List<CreditHistory> creditHistories) {
        paymentRepository.save(payment);
        if(failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.getLast());
        }
    }

    private boolean publishIfOutboxMessageProcessedForPayment(PaymentRequest paymentRequest, PaymentStatus paymentStatus) {
        return orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(UUID.fromString(paymentRequest.getSagaId()), paymentStatus)
                .map(orderOutboxMessage -> {
                    paymentResponseMessagePublisher.publish(orderOutboxMessage, orderOutboxHelper::updateOutboxMessage);
                    return true;
                }).orElse(false);
    }
}
