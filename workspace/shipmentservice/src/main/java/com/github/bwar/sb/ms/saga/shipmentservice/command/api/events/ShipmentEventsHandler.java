package com.github.bwar.sb.ms.saga.shipmentservice.command.api.events;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.github.bwar.sb.ms.saga.commonservice.events.OrderShippedEvent;
import com.github.bwar.sb.ms.saga.shipmentservice.command.api.data.Shipment;
import com.github.bwar.sb.ms.saga.shipmentservice.command.api.data.ShipmentRepository;

@Component
public class ShipmentEventsHandler {
	
	private ShipmentRepository shipmentRepository;
	
	
	public ShipmentEventsHandler(ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}


	@EventHandler
	public void on(OrderShippedEvent event) {
		/*
		Shipment shipment = Shipment.builder()
				.shipmentId(event.getShipmentId())
				.orderId(event.getOrderId())
				.shipmentStatus(event.getShipmentStatus())
				.build();
		*/
		Shipment shipment = new Shipment();
		BeanUtils.copyProperties(event, shipment);
		shipmentRepository.save(shipment);
		
	}
}
