package com.github.bwar.sb.ms.saga.api.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.github.bwar.sb.ms.saga.command.api.command.CreateOrderCommand;
import com.github.bwar.sb.ms.saga.command.api.events.OrderCreatedEvent;
import com.github.bwar.sb.ms.saga.commonservice.commands.CancelOrderCommand;
import com.github.bwar.sb.ms.saga.commonservice.commands.CompleteOrderCommand;
import com.github.bwar.sb.ms.saga.commonservice.events.OrderCancelledEvent;
import com.github.bwar.sb.ms.saga.commonservice.events.OrderCompletedEvent;

@Aggregate
public class OrderAggregate {
	
	@AggregateIdentifier
	private String orderId;
	private String productId;
	private String userId;
	private String addressId;
	private Integer quantity;
	private String orderStatus;
	
	public OrderAggregate() {}
	
	@CommandHandler
	public OrderAggregate(CreateOrderCommand createOrderCommand) {
		
		//Validate the command object before applying aggregate
		OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
				.orderId(createOrderCommand.getOrderId())
				.productId(createOrderCommand.getProductId())
				.userId(createOrderCommand.getUserId())
				.addressId(createOrderCommand.getAddressId())
				.quantity(createOrderCommand.getQuantity())
				.orderStatus(createOrderCommand.getOrderStatus())
				.build();
		
		AggregateLifecycle.apply(orderCreatedEvent);
		
	}
	
	//Handle the event
	@EventSourcingHandler
	public void on(OrderCreatedEvent orderCreatedEvent) {
		this.orderId = orderCreatedEvent.getOrderId();
		this.productId = orderCreatedEvent.getProductId();
		this.userId = orderCreatedEvent.getUserId();
		this.addressId = orderCreatedEvent.getAddressId();
		this.quantity = orderCreatedEvent.getQuantity();
		this.orderStatus = orderCreatedEvent.getOrderStatus();
		
	}
	
	@CommandHandler
	public void handle(CompleteOrderCommand completeOrderCommand) {
		//publish order compelted Event
		OrderCompletedEvent orderCompletedEvent =
				OrderCompletedEvent.builder()
				.orderId(completeOrderCommand.getOrderId())
				.orderStatus(completeOrderCommand.getOrderStatus())
				.build();
		AggregateLifecycle.apply(orderCompletedEvent);
 		
	}
	
	@EventSourcingHandler
	public void on(OrderCompletedEvent event) {
		this.orderStatus= event.getOrderStatus();
	}
	
	@CommandHandler
	public void handle(CancelOrderCommand cancelOrderCommand) {
		
		OrderCancelledEvent orderCancelledEvent = 
				new OrderCancelledEvent();
		BeanUtils.copyProperties(cancelOrderCommand, orderCancelledEvent);
		AggregateLifecycle.apply(orderCancelledEvent);
		
	}
	
	@EventSourcingHandler
	public void on(OrderCancelledEvent event) {
		
		this.orderStatus=event.getOrderStatus();
		
	}
}
