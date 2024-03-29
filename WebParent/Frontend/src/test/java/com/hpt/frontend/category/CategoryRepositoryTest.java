package com.hpt.frontend.category;

import com.hpt.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository repo;

    @Test
    public void testListEnabledCategories() {
        List<Category> categories = repo.findAllByEnabledTrueOrderByNameAsc();
        categories.forEach(category -> System.out.println(category.getName() + " (" + category.isEnabled() + ")"));
    }

    @Test
    public void testFindCategoryByAlias() {
        String alias = "Laptop";
        Category category = repo.findByAliasAndEnabledTrue(alias);
        System.out.println(category);

        assertThat(category).isNotNull();
    }
}