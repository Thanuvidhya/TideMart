package com.tidemart.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SizeChartRepository extends JpaRepository<SizeChart, Long> {
    Optional<SizeChart> findByProductId(Long productId);
}
