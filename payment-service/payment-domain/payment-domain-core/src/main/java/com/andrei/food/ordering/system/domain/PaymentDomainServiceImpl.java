package com.andrei.food.ordering.system.domain;

import com.andrei.food.ordering.system.domain.entity.CreditEntry;
import com.andrei.food.ordering.system.domain.entity.CreditHistory;
import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.domain.event.PaymentCancelledEvent;
import com.andrei.food.ordering.system.domain.event.PaymentCompletedEvent;
import com.andrei.food.ordering.system.domain.event.PaymentEvent;
import com.andrei.food.ordering.system.domain.event.PaymentFailedEvent;
import com.andrei.food.ordering.system.domain.valueobject.CreditHistoryId;
import com.andrei.food.ordering.system.domain.valueobject.TransactionType;
import com.andrei.food.ordering.system.service.DomainConstants;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;
import com.andrei.food.ordering.system.service.valueobject.Money;
import com.andrei.food.ordering.system.service.valueobject.PaymentStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {

    @Override
    public PaymentEvent validateAndInitiatePayment(Payment payment, CreditEntry creditEntry, List<CreditHistory> creditHistories, List<String> failureMessages) {
        payment.validatePayment(failureMessages);
        payment.initializePayment();
        validateCreditEntry(payment, creditEntry, failureMessages);
        subtractCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.DEBIT);
        validateCreditHistory(creditEntry, creditHistories, failureMessages);

        if (!failureMessages.isEmpty()) {
            log.info("Payment initiation for customer with id: {} has failed", payment.getCustomerId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)), failureMessages);
        }

        log.info("Payment for customer with id: {} has been successfully initiated", payment.getCustomerId().getValue());
        payment.updateStatus(PaymentStatus.COMPLETED);
        return new PaymentCompletedEvent(payment, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)));
    }

    @Override
    public PaymentEvent validateAndCancelPayment(Payment payment, CreditEntry creditEntry, List<CreditHistory> creditHistories, List<String> failureMessages) {
        payment.validatePayment(failureMessages);
        addCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.CREDIT);

        if(!failureMessages.isEmpty()){
            log.info("Payment cancellation for customer with id: {} has failed", payment.getCustomerId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)), failureMessages);
        }
        log.info("Payment for customer with id: {} has been successfully cancelled", payment.getCustomerId().getValue());
        payment.updateStatus(PaymentStatus.CANCELLED);
        return new PaymentCancelledEvent(payment, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)));
    }

    private void validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {
        if(payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())){
            log.error("Customer with id: {} does not have enough credit to pay for the order", payment.getCustomerId().getValue());
            failureMessages.add("Customer with id: "+payment.getCustomerId().getValue()+" does not have enough credit to pay for the order");
        }
    }


    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.subtractCreditAmount(payment.getPrice());
    }

    private void updateCreditHistory(Payment payment, List<CreditHistory> creditHistories, TransactionType transactionType) {
        creditHistories.add(CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .transactionType(transactionType)
                .amount(payment.getPrice())
                .build());
    }

    private void validateCreditHistory(CreditEntry creditEntry, List<CreditHistory> creditHistories, List<String> failureMessages) {
        Money totalCreditHistory = getTotalHistoryAmount(creditHistories, TransactionType.CREDIT);
        Money totalDebitHistory = getTotalHistoryAmount(creditHistories, TransactionType.DEBIT);

        if(totalDebitHistory.isGreaterThan(totalCreditHistory)){
            log.error("Customer with id: {} does not have enough credit according to credit history", creditEntry.getCustomerId().getValue());
            failureMessages.add("Customer with id: "+creditEntry.getCustomerId().getValue()+" does not have enough credit according to credit history");
        }

        if(!creditEntry.getTotalCreditAmount().equals(totalCreditHistory.subtract(totalDebitHistory))){
            log.error("Credit entry total credit amount does not match with credit history for customer Id: {}", creditEntry.getCustomerId().getValue());
            failureMessages.add("Credit entry total credit amount does not match with credit history for customer Id: "+creditEntry.getCustomerId().getValue());
        }
    }

    private static Money getTotalHistoryAmount(List<CreditHistory> creditHistories, TransactionType transactionType) {
        return creditHistories.stream()
                .filter(creditHistory -> creditHistory.getTransactionType().equals(transactionType))
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);
    }

    private void addCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.addCreditAmount(payment.getPrice());
    }
}
