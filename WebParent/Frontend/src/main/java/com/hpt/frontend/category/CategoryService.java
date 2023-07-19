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
}
