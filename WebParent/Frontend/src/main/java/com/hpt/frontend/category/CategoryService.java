package com.hpt.frontend.category;

import com.hpt.common.entity.Category;
import com.hpt.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repo;

    public static final String SORT_FIELD_NAME = "name";

    /**
     * Get all categories that not children categories enabled and sort by name ascending
     *
     * @return a list of categories that not children categories
     */
    public List<Category> listNotChildrenCategories() {
        List<Category> listNotChildrenCategories = new ArrayList<>();

        List<Category> listEnabledCategories = repo.findAllByEnabledTrueOrderByNameAsc();

        listEnabledCategories.forEach(category -> {
            if (category.getParent() == null) {
                listNotChildrenCategories.add(category);
            }
        });

        return listNotChildrenCategories;
    }


    /**
     * Get a category by alias with enabled as true
     *
     * @param alias the alias of category
     * @return a category by alias
     */
    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category category = repo.findByAliasAndEnabledTrue(alias);
        if (category == null) {
            throw new CategoryNotFoundException("Could not find any category with alias " + alias);
        }
        return category;
    }

    /**
     * Return a list of category parents of a child category and child category
     *
     * @param child the child category
     * @return a list of category parents and child
     */
    public List<Category> getCategoryParents(Category child) {
        List<Category> listParents = new ArrayList<>();

        Category parent = child.getParent();

        while (parent != null) {
            listParents.add(0, parent);
            parent = parent.getParent();
        }

        listParents.add(child);

        return listParents;
    }
}
