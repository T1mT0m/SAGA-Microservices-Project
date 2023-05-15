package com.github.bwar.sb.ms.saga.commonservice.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class CancelPaymentCommand {
	
	@TargetAggregateIdentifier
	private String paymentId;
	private String orderId;
	private String paymentStatus = "CANCELLED";
	

}
