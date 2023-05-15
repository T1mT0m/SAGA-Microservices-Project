package com.github.bwar.sb.ms.saga.commonservice.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class CancelOrderCommand {
	
	@TargetAggregateIdentifier
	private String orderId;
	private String orderStatus="CANCELLED";

}
