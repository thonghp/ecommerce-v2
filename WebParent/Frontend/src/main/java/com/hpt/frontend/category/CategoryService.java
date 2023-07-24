package com.hpt.frontend.category;

import com.hpt.common.entity.Category;
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
     * Get all root categories with enabled as true and sort by name ascending
     *
     * @return a list of root categories
     */
    public List<Category> listRootCategories() {
        List<Category> rootCategories = new ArrayList<>();

        List<Category> listEnabledCategories = repo.findAllByEnabledTrueOrderByNameAsc();

        listEnabledCategories.forEach(category -> {
            if (category.getParent() == null) {
                rootCategories.add(category);
            }
        });

        return rootCategories;
    }


    /**
     * Get a category by alias with enabled as true
     *
     * @param alias the alias of category
     * @return a category by alias
     */
    public Category getCategory(String alias) {
        return repo.findByAliasAndEnabledTrue(alias);
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
