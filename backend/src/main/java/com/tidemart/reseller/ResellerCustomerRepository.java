package com.tidemart.reseller;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResellerCustomerRepository extends JpaRepository<ResellerCustomer, Long> {
    List<ResellerCustomer> findByResellerIdOrderByIdDesc(Long resellerId);
    Optional<ResellerCustomer> findByResellerIdAndPhone(Long resellerId, String phone);
}
