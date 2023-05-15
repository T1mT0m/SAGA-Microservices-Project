package com.github.bwar.sb.ms.saga.commonservice.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShipOrderCommand {
	
	@TargetAggregateIdentifier
	private String shipmentId;
	private String orderId;

}
