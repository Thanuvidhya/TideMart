package com.tidemart.reseller;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResellerRepository extends JpaRepository<Reseller, Long> {
    Optional<Reseller> findByUserId(Long userId);
}
