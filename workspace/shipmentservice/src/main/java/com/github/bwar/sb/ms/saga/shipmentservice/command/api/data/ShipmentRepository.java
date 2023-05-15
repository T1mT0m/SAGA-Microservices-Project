package com.github.bwar.sb.ms.saga.shipmentservice.command.api.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, String>{

}
