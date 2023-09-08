package com.hpt.frontend.category;

import com.hpt.common.entity.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {
    List<Category> findAllByEnabledTrueOrderByNameAsc();

    Category findByAliasAndEnabledTrue(String alias);
}
