package com.hpt.backend.category;

import com.hpt.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listAll(Model model) {
        List<Category> categories = categoryService.listAll();

        model.addAttribute("categories", categories);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        Category category = new Category();
        List<Category> categories = categoryService.listHierarchicalCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("category", category);
        model.addAttribute("pageTitle", "Thêm thể loại");

        return "categories/category_form";
    }
}
