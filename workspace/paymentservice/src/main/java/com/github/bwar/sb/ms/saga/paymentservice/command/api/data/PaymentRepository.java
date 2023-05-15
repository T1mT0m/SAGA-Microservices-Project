package com.github.bwar.sb.ms.saga.paymentservice.command.api.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String>{

}
