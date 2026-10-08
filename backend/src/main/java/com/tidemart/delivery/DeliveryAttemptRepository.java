package com.tidemart.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryAttemptRepository extends JpaRepository<DeliveryAttempt, Long> {
    List<DeliveryAttempt> findByOrderIdOrderByIdAsc(Long orderId);
    List<DeliveryAttempt> findByPartnerIdOrderByIdDesc(Long partnerId);
}
