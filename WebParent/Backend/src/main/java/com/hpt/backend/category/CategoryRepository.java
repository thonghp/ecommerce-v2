package com.hpt.backend.category;

import com.hpt.common.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
    List<Category> findByParentIsNull(Sort sort);

    Category findByName(String name);

    Category findByAlias(String alias);
}
