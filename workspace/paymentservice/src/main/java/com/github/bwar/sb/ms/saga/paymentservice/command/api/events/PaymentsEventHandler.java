package com.github.bwar.sb.ms.saga.paymentservice.command.api.events;

import java.util.Date;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.github.bwar.sb.ms.saga.commonservice.events.PaymentCancelledEvent;
import com.github.bwar.sb.ms.saga.commonservice.events.PaymentProcessedEvent;
import com.github.bwar.sb.ms.saga.paymentservice.command.api.data.Payment;
import com.github.bwar.sb.ms.saga.paymentservice.command.api.data.PaymentRepository;

@Component
public class PaymentsEventHandler {
	
	private PaymentRepository paymentRepository;
	
	public PaymentsEventHandler(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}

	@EventHandler
	public void on(PaymentProcessedEvent event) {
		Payment payment = Payment.builder()
				.paymentId(event.getPaymentId())
				.orderId(event.getOrderId())
				.paymentStatus("COMPLETED")
				.timeStamp(new Date())
				.build();
		
		paymentRepository.save(payment);
		
	}
	
	@EventHandler
	public void on(PaymentCancelledEvent event) {
		Payment payment = paymentRepository.findById(event.getPaymentId()).get();
		payment.setPaymentStatus(event.getPaymentStatus());
		paymentRepository.save(payment);
	}


}
