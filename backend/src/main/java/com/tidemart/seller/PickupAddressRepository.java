package com.tidemart.seller;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PickupAddressRepository extends JpaRepository<PickupAddress, Long> {
    Optional<PickupAddress> findBySellerId(Long sellerId);
}
