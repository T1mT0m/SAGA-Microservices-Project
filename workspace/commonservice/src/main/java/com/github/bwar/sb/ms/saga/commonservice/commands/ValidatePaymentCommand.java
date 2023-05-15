package com.github.bwar.sb.ms.saga.commonservice.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.github.bwar.sb.ms.saga.commonservice.model.CardDetails;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidatePaymentCommand {
	@TargetAggregateIdentifier
	private String paymentId;
	private String orderid;
	private CardDetails cardDetails;

}
