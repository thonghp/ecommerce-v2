package com.hpt.backend.category;

import com.hpt.common.entity.Category;
import com.hpt.common.exception.CateogryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service

public class CategoryService {
    @Autowired
    private CategoryRepository repo;

    /**
     * Returns a list of categories in hierarchical order to display in list view
     *
     * @return List of categories
     */
    public List<Category> listAll() {
        return listHierarchicalCategories(repo.findByParentIsNull());
    }

    /**
     * Returns a list of categories in hierarchical order
     *
     * @return List of hierarchical categories
     */
    private List<Category> listHierarchicalCategories(List<Category> rootCategories) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));
            Set<Category> children = rootCategory.getChildren();

            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubCategories(hierarchicalCategories, subCategory, 1);
            }
        }

        return hierarchicalCategories;
    }

    /**
     * Returns a list of children categories in hierarchical order
     *
     * @param categories List of categories
     * @param parent     Parent category
     * @param subLevel   Sub level
     */
    public void listSubCategories(List<Category> categories, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            String name = "";

            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }

            categories.add(Category.copyFull(subCategory, name + subCategory.getName()));

            listSubCategories(categories, subCategory, newSubLevel);
        }
    }

    /**
     * Returns a list of categories with only id and name in hierarchical order to use in the form
     *
     * @return List of hierarchical categories
     */
    public List<Category> listHierarchicalCategoriesInform() {
        List<Category> hierarchicalCategories = new ArrayList<>();
        List<Category> rootCategories = repo.findByParentIsNull();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyIdAndName(rootCategory));
            Set<Category> children = rootCategory.getChildren();

            for (Category subCategory : children) {
                Integer id = subCategory.getId();
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyIdAndName(id, name));

                listSubCategoriesInform(hierarchicalCategories, subCategory, 1);
            }
        }

        return hierarchicalCategories;
    }

    /**
     * Returns a list of children categories with only id and name in hierarchical order to use in the form
     *
     * @param categories List of categories
     * @param parent     Parent category
     * @param subLevel   Sub level
     */
    public void listSubCategoriesInform(List<Category> categories, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            String name = "";

            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }

            Category child = Category.copyIdAndName(subCategory.getId(), name + subCategory.getName());
            categories.add(child);

            listSubCategoriesInform(categories, subCategory, newSubLevel);
        }
    }

    /**
     * Save category information. If the id does not exist, save the category, otherwise the id already exists, update the category
     *
     * @param category category object to save
     * @return saved category object
     */
    public Category save(Category category) {
        if (category.getAlias() == null || category.getAlias().isEmpty()) {
            String defaultAlias = category.getName().replaceAll(" ", "-");
            category.setAlias(defaultAlias);
        } else {
            category.setAlias(category.getAlias().replaceAll(" ", "-"));
        }

        return repo.save(category);
    }

    /**
     * Get a category by id
     *
     * @param id id of the category
     * @return category object corresponding to id
     * @throws CateogryNotFoundException if the category does not exist
     */
    public Category get(Integer id) throws CateogryNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (Exception ex) {
            throw new CateogryNotFoundException("Không tìm thấy thể loại có id là " + id);
        }
    }
}
