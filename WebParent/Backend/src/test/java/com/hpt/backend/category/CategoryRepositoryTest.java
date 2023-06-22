package com.hpt.backend.category;

import com.hpt.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository repo;

    @Test
    public void testCreateRootCategory() {
        Category category = new Category();
        category.setName("Default");
        category.setAlias("default");
        category.setImage("default.jpg");

        Category savedCategory = repo.save(category);
        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateSubCategory() {
        Category parent = new Category();
        parent.setId(1);

        Category category = new Category();
        category.setName("Default Child");
        category.setAlias("default-child");
        category.setImage("child.jpg");
        category.setParent(parent);

        Category savedCategory = repo.save(category);
        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetCategoryById() {
        Category category = repo.findById(1).get();
        assertThat(category).isNotNull();
    }

    @Test
    public void testUpdateCategoryDetails() {
        Category category = repo.findById(2).get();
        category.setName("Child");
        category.setAlias("child");
        category.setImage("child.jpg");

        Category savedCategory = repo.save(category);

        assertThat(savedCategory.getName()).isEqualTo("Child");
    }

    // Lưu ý Có ràng buộc khóa ngoại nên phải xóa các bản ghi con trước
    @Test
    public void testDeleteUser() {
        Integer categoryId = 2;
        repo.deleteById(categoryId);
    }

    @Test
    public void testPrintHierarchicalCategories() {
        Iterable<Category> categories = repo.findAll();

        for (Category category : categories) {
            if (category.getParent() == null) {
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    System.out.println("--" + subCategory.getName());
                    testPrintChildren(subCategory, 1);
                }
            }
        }
    }

    @Test
    public void testPrintChildren(Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            for (int i = 0; i < newSubLevel; i++) {
                System.out.print("--");
            }

            System.out.println(subCategory.getName());

            testPrintChildren(subCategory, newSubLevel);
        }
    }
}
