package com.tidemart.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CodCollectionRepository extends JpaRepository<CodCollection, Long> {
}
