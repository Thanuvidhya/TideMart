package com.tidemart.cms;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PolicyPageRepository extends JpaRepository<PolicyPage, Long> {
    Optional<PolicyPage> findBySlug(String slug);
}
