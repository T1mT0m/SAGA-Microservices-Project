package com.github.bwar.sb.ms.saga.commonservice.events;

import lombok.Data;

@Data
public class OrderCancelledEvent {
	
	private String orderId;
	private String orderStatus;

}
