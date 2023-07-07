package com.hpt.backend.brand;

import com.hpt.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {
    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> search(String keyword, Pageable pageable);

    @Query("UPDATE Brand SET enabled = ?2 WHERE id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    Long countById(Integer id);

    Brand findByName(String name);
}
