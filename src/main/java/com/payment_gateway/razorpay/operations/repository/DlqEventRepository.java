package com.payment_gateway.razorpay.operations.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.payment_gateway.razorpay.operations.entity.DlqEvent;

public interface DlqEventRepository extends JpaRepository<DlqEvent, UUID> {

}
