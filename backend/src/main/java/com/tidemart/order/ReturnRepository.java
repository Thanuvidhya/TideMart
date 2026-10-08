package com.tidemart.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReturnRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByOrderIdInOrderByIdDesc(List<Long> orderIds);
    Optional<ReturnRequest> findByOrderItemId(Long orderItemId);
}
