package com.tidemart.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p where p.status = 'APPROVED' and (:cat = 0 or p.categoryId = :cat) and lower(p.name) like lower(concat('%', :q, '%')) and p.price >= :min and p.price <= :max")
    Page<Product> search(@Param("q") String q, @Param("cat") long cat, @Param("min") double min, @Param("max") double max, Pageable pageable);

    List<Product> findTop6ByCategoryIdAndIdNotAndStatusOrderByRatingCountDesc(Long categoryId, Long id, String status);
    List<Product> findTop24ByStatusOrderByRatingCountDesc(String status);
    List<Product> findTop12ByCategoryIdAndStatusOrderByRatingCountDesc(Long categoryId, String status);
    List<Product> findTop5ByStatusAndNameContainingIgnoreCase(String status, String q);
    List<Product> findBySellerIdOrderByIdDesc(Long sellerId);
}
