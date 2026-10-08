package com.tidemart.reseller;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResellerProductRepository extends JpaRepository<ResellerProduct, Long> {
    Optional<ResellerProduct> findByResellerIdAndProductId(Long resellerId, Long productId);
}
