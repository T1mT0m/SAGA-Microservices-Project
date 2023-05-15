package com.github.bwar.sb.ms.saga.paymentservice.command.api.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.github.bwar.sb.ms.saga.commonservice.commands.CancelPaymentCommand;
import com.github.bwar.sb.ms.saga.commonservice.commands.ValidatePaymentCommand;
import com.github.bwar.sb.ms.saga.commonservice.events.PaymentCancelledEvent;
import com.github.bwar.sb.ms.saga.commonservice.events.PaymentProcessedEvent;


import lombok.extern.slf4j.Slf4j;

@Aggregate
@Slf4j
public class PaymentAggregate {
	@AggregateIdentifier
	private String paymentId;
	private String orderid;
	private String paymentStatus;
	
	public PaymentAggregate() {	}
	
	@CommandHandler
	public PaymentAggregate(ValidatePaymentCommand validatePaymentCommand) {	
		//validate the payment details
		//Publish the payment processed event
		log.info("Executing validatePaymentCommand for Order ID: {} and "
				+ "payment id : {}", validatePaymentCommand.getOrderid(),
				validatePaymentCommand.getPaymentId());
		
		PaymentProcessedEvent paymentProcessedEvent =
				new PaymentProcessedEvent(
						validatePaymentCommand.getPaymentId(),
						validatePaymentCommand.getOrderid());
		
		AggregateLifecycle.apply(paymentProcessedEvent);
		log.info("PaymentProcessedEvent applied");
	}
	
	@EventSourcingHandler
	public void on(PaymentProcessedEvent paymentProcessedEvent) {
		this.paymentId =paymentProcessedEvent.getPaymentId();
		this.orderid = paymentProcessedEvent.getOrderId();
	}
	
	@CommandHandler
	public void handle(CancelPaymentCommand cancelPaymentCommand) {
		PaymentCancelledEvent paymentCancelledEvent = 
				new PaymentCancelledEvent();
		BeanUtils.copyProperties(cancelPaymentCommand, paymentCancelledEvent);
		AggregateLifecycle.apply(paymentCancelledEvent);
	}
	
	@EventSourcingHandler
	public void on(PaymentCancelledEvent paymentCancelledEvent) {
		this.paymentStatus=paymentCancelledEvent.getPaymentStatus();
	}

}
