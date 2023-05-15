package com.github.bwar.sb.ms.saga.command.api.events;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.github.bwar.sb.ms.saga.command.api.data.Order;
import com.github.bwar.sb.ms.saga.command.api.data.OrderRepository;
import com.github.bwar.sb.ms.saga.commonservice.events.OrderCancelledEvent;
import com.github.bwar.sb.ms.saga.commonservice.events.OrderCompletedEvent;

@Component
public class OrderEventsHandler {
	
	private OrderRepository orderRepository;
	
	public OrderEventsHandler(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@EventHandler
	public void on(OrderCreatedEvent orderCreatedEvent) {
		Order order = Order.builder()
				.orderId(orderCreatedEvent.getOrderId())
				.addressId(orderCreatedEvent.getAddressId())
				.productId(orderCreatedEvent.getProductId())
				.userId(orderCreatedEvent.getUserId())
				.quantity(orderCreatedEvent.getQuantity())
				.orderStatus(orderCreatedEvent.getOrderStatus())
				.build();				
		orderRepository.save(order);
	}
	
	@EventHandler
	public void on(OrderCompletedEvent orderCompletedEvent) {
		Order order = orderRepository.
				findById(orderCompletedEvent.getOrderId()).get();
		order.setOrderId(orderCompletedEvent.getOrderStatus());
		orderRepository.save(order);
	}
	
	@EventHandler
	public void on(OrderCancelledEvent event) {
		Order order = orderRepository.
				findById(event.getOrderId()).get();
		order.setOrderId(event.getOrderStatus());
		orderRepository.save(order);		
	}

}
