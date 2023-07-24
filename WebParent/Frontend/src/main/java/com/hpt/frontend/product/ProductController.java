package com.hpt.frontend.product;

import com.hpt.common.entity.Category;
import com.hpt.common.entity.Product;
import com.hpt.common.utils.PageInfo;
import com.hpt.frontend.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller

public class ProductController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
                                        Model model) {
        return viewCategoryByPage(alias, 1, model);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias,
                                     @PathVariable(name = "pageNum") int pageNum, Model model) {
        Category category = categoryService.getCategory(alias);
        if (category == null) {
            return "error/404";
        }

        List<Category> listCategoryParents = categoryService.getCategoryParents(category);
        PageInfo pageInfo = new PageInfo();
        List<Product> products = productService.listByCategory(pageInfo, pageNum, category.getId());
        long startCount = pageInfo.getStartCount(pageNum, ProductService.PRODUCTS_PER_PAGE);
        long endCount = pageInfo.getEndCount(pageNum, ProductService.PRODUCTS_PER_PAGE);

        model.addAttribute("pageTitle", category.getName());
        model.addAttribute("listCategoryParents", listCategoryParents);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("products", products);
        model.addAttribute("category", category);

        return "products_by_category";
    }
}
