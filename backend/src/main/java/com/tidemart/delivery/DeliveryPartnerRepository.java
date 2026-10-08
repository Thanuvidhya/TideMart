package com.tidemart.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    java.util.Optional<DeliveryPartner> findByPhone(String phone);
}
