package com.andrei.food.ordering.system.domain;

import com.andrei.food.ordering.system.domain.entity.CreditEntry;
import com.andrei.food.ordering.system.domain.entity.CreditHistory;
import com.andrei.food.ordering.system.domain.entity.Payment;
import com.andrei.food.ordering.system.domain.event.PaymentCancelledEvent;
import com.andrei.food.ordering.system.domain.event.PaymentCompletedEvent;
import com.andrei.food.ordering.system.domain.event.PaymentEvent;
import com.andrei.food.ordering.system.domain.event.PaymentFailedEvent;
import com.andrei.food.ordering.system.service.events.publisher.DomainEventPublisher;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment,
                                            CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories,
                                            List<String> failureMessages);
    PaymentEvent validateAndCancelPayment(Payment payment,
                                          CreditEntry creditEntry,
                                          List<CreditHistory> creditHistories,
                                            List<String> failureMessages);
}
