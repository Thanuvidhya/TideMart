package com.tidemart.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
    List<StockAlert> findByProductId(Long productId);
    Optional<StockAlert> findByUserIdAndProductId(Long userId, Long productId);
}
