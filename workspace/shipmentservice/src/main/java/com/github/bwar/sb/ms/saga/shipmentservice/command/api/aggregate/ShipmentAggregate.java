package com.github.bwar.sb.ms.saga.shipmentservice.command.api.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.github.bwar.sb.ms.saga.commonservice.commands.ShipOrderCommand;
import com.github.bwar.sb.ms.saga.commonservice.events.OrderShippedEvent;

@Aggregate
public class ShipmentAggregate {
	
	@AggregateIdentifier
	private String shipmentId;
	private String orderId;
	private String shipmentStatus;
	
	public ShipmentAggregate() {}
	
	@CommandHandler
	public ShipmentAggregate(ShipOrderCommand shipOrderCommand) {
		//validate the command
		//publish the Order shipped event
		OrderShippedEvent orderShippedEvent =
				OrderShippedEvent.builder()
				.shipmentId(shipOrderCommand.getShipmentId())
				.orderId(shipOrderCommand.getOrderId())
				.shipmentStatus("COMPLETED")
				.build();
		AggregateLifecycle.apply(orderShippedEvent);
			
	}
	
	@EventSourcingHandler
	public void on(OrderShippedEvent event) {
		this.shipmentId = event.getShipmentId();
		this.orderId = event.getOrderId();
		this.shipmentStatus=event.getShipmentStatus();
		
	}

}
