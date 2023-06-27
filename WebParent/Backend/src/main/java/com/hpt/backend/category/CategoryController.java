package com.hpt.backend.category;

import com.hpt.backend.FileUploadUtils;
import com.hpt.common.entity.Category;
import com.hpt.common.entity.Role;
import com.hpt.common.entity.User;
import com.hpt.common.exception.CateogryNotFoundException;
import com.hpt.common.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
        List<Category> categories = categoryService.listHierarchicalCategoriesInform();

        model.addAttribute("categories", categories);
        model.addAttribute("category", category);
        model.addAttribute("pageTitle", "Thêm thể loại");

        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, RedirectAttributes redirectAttributes,
                               @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            category.setImagePath(fileName);
            Category savedCategory = categoryService.save(category);

            String uploadDir = "category-photos/" + savedCategory.getId();

            FileUploadUtils.cleanDir(uploadDir); // remove ảnh cũ trước khi lưu ảnh mới
            FileUploadUtils.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (category.getImagePath().isEmpty()) category.setImagePath(null);
            categoryService.save(category);
        }

        String name = category.getName();
        redirectAttributes.addFlashAttribute("message", "Thể loại " + name + " đã được lưu thành công");

        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.get(id);
            List<Category> categories = categoryService.listHierarchicalCategoriesInform();

            model.addAttribute("category", category);
            model.addAttribute("categories", categories);
            model.addAttribute("pageTitle", "Chỉnh sửa thể loại");

            return "categories/category_form";
        } catch (CateogryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());

            return "redirect:/categories";
        }
    }
}
