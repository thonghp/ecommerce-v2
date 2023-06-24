package com.hpt.backend.category;

import com.hpt.common.entity.Category;
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
     * Return a list of all users
     *
     * @return List of users
     */
    public List<Category> listAll() {
        return (List<Category>) repo.findAll();
    }

    /**
     * Return a list of categories in hierarchical order
     *
     * @return List of hierarchical categories
     */
    public List<Category> listHierarchicalCategories() {
        List<Category> list = new ArrayList<>();

        Iterable<Category> categories = repo.findAll();

        for (Category category : categories) {
            if (category.getParent() == null) {
                Category parent = new Category();
                parent.setName(category.getName());
                list.add(parent);

                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    Category child = new Category();
                    child.setName(name);
                    list.add(child);

                    listChildren(list, subCategory, 1);
                }
            }
        }

        return list;
    }

    /**
     * Return a list of children categories
     *
     * @param categories List of categories
     * @param parent     Parent category
     * @param subLevel   Sub level
     */
    public void listChildren(List<Category> categories, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }

            Category child = new Category();
            child.setName(name + subCategory.getName());
            categories.add(child);

            listChildren(categories, subCategory, newSubLevel);
        }
    }
}
