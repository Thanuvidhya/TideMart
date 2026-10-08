package com.tidemart.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductAnswerRepository extends JpaRepository<ProductAnswer, Long> {
    List<ProductAnswer> findByQuestionIdOrderByIdAsc(Long questionId);
}
