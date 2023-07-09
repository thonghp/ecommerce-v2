package com.hpt.backend.product;

import com.hpt.common.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
    Product findByName(String name);

    @Query("UPDATE Product SET enabled = ?2 WHERE id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    Long countById(Integer id);
}
