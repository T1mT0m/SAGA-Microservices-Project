package com.github.bwar.sb.ms.saga.command.api.saga;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;

import com.github.bwar.sb.ms.saga.command.api.events.OrderCreatedEvent;
import com.github.bwar.sb.ms.saga.commonservice.commands.CancelOrderCommand;
import com.github.bwar.sb.ms.saga.commonservice.commands.CancelPaymentCommand;
import com.github.bwar.sb.ms.saga.commonservice.commands.CompleteOrderCommand;
import com.github.bwar.sb.ms.saga.commonservice.commands.ShipOrderCommand;
import com.github.bwar.sb.ms.saga.commonservice.commands.ValidatePaymentCommand;
import com.github.bwar.sb.ms.saga.commonservice.events.OrderCancelledEvent;
import com.github.bwar.sb.ms.saga.commonservice.events.OrderCompletedEvent;
import com.github.bwar.sb.ms.saga.commonservice.events.OrderShippedEvent;
import com.github.bwar.sb.ms.saga.commonservice.events.PaymentCancelledEvent;
import com.github.bwar.sb.ms.saga.commonservice.events.PaymentProcessedEvent;
import com.github.bwar.sb.ms.saga.commonservice.model.User;
import com.github.bwar.sb.ms.saga.commonservice.queries.GetUserPaymentDetailsQuery;

import lombok.extern.slf4j.Slf4j;

@Saga
@Slf4j
public class OrderProcessingSaga {
	
	@Autowired
	private transient CommandGateway commandGateway;
	@Autowired
	private transient QueryGateway queryGateway;
	/*
	public OrderProcessingSaga(CommandGateway commandGateway, QueryGateway queryGateway) {
		this.commandGateway = commandGateway;
		this.queryGateway = queryGateway;
	}
	*/

	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	private void handle (OrderCreatedEvent orderCreatedEvent) {
		log.info("OrderCreatedEvent in SAGA for order id : {}", 
				orderCreatedEvent.getOrderId());
		
		GetUserPaymentDetailsQuery getUserPaymentDetailsQuery = 
				new GetUserPaymentDetailsQuery(orderCreatedEvent.getUserId());
		
		User user = null;		
		
		try {
			user = queryGateway.query(
					getUserPaymentDetailsQuery, 
					ResponseTypes.instanceOf(User.class)).join();
		}catch(Exception e) {
			log.error(e.getMessage());
			//start the compensating transactions
			cancelOrderCommand(orderCreatedEvent.getOrderId());
			
		}
		
		ValidatePaymentCommand validatePaymentCommand = 
				ValidatePaymentCommand.builder()
				.orderid(orderCreatedEvent.getOrderId())
				.paymentId(UUID.randomUUID().toString())
				.cardDetails(user.getCardDetails())
				.build();
		
		commandGateway.sendAndWait(validatePaymentCommand);
	}
	
	private void cancelOrderCommand(String orderId) {
		CancelOrderCommand command = new CancelOrderCommand(orderId);
		commandGateway.send(command);
		
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	@EndSaga
	public void handle(OrderCancelledEvent event) {
		log.info("OrderCancelledEvent in SAGA for order id : {}", 
				event.getOrderId());		
	}

	@SagaEventHandler(associationProperty = "orderId")
	private void handle(PaymentProcessedEvent event) {
		log.info("PaymentProcessedEvent in SAGA for order id : {}", 
				event.getOrderId());
		try {
			ShipOrderCommand shipOrderCommand =
					ShipOrderCommand.builder()
					.shipmentId(UUID.randomUUID().toString())
					.orderId(event.getOrderId())
					.build();
			
			commandGateway.send(shipOrderCommand);
			
		} catch (Exception e) {
			log.error(e.getMessage());
			//start the compensation transaction
			cancelPaymentCommand(event);
		}
	}
	
	private void cancelPaymentCommand(PaymentProcessedEvent event) {
		CancelPaymentCommand cancelPaymentCommand = 
				new CancelPaymentCommand(event.getPaymentId(),
						event.getOrderId());
		commandGateway.send(cancelPaymentCommand);
	}

	@SagaEventHandler(associationProperty = "orderId")
	private void handle(OrderShippedEvent event) {
		log.info("OrderShippedEvent in SAGA for order id : {}", 
				event.getOrderId());
		
		CompleteOrderCommand completeOrderCommand =
				CompleteOrderCommand.builder()
				.orderId(event.getOrderId())
				.orderStatus("APPROVED")
				.build();
		commandGateway.send(completeOrderCommand);
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	@EndSaga
	public void handle(OrderCompletedEvent event) {
		log.info("OrderCompletedEvent in SAGA for order id : {}", 
				event.getOrderId());		
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentCancelledEvent event) {
		log.info("PaymentCancelledEvent in SAGA for order id : {}", 
				event.getOrderId());
		cancelOrderCommand(event.getOrderId());
		
	}

}
