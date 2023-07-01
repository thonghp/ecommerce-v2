package com.hpt.backend.category.controller;

import com.hpt.backend.FileUploadUtils;
import com.hpt.backend.category.CategoryService;
import com.hpt.common.entity.Category;
import com.hpt.common.exception.CategoryNotFoundException;
import com.hpt.common.utils.PageInfo;
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
    private CategoryService service;

    @GetMapping("/categories")
    public String listFirstPage(Model model) {
        return listByPage(1, model, CategoryService.ASCENDING);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @RequestParam(name = "sortType") String sortType) {
        PageInfo pageInfo = new PageInfo();
        List<Category> categories = service.listByPage(pageInfo, pageNum, sortType);
        String reverseSortType = sortType.equals(CategoryService.ASCENDING) ? CategoryService.DESCENDING : CategoryService.ASCENDING;

        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortType", sortType);
        model.addAttribute("categories", categories);
        model.addAttribute("reverseSortType", reverseSortType);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        Category category = new Category();
        List<Category> categories = service.listHierarchicalCategoriesInform();

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
            Category savedCategory = service.save(category);

            String uploadDir = "category-photos/" + savedCategory.getId();

            FileUploadUtils.cleanDir(uploadDir); // remove ảnh cũ trước khi lưu ảnh mới
            FileUploadUtils.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (category.getImagePath().isEmpty()) category.setImagePath(null);
            service.save(category);
        }

        String name = category.getName();
        redirectAttributes.addFlashAttribute("message", "Thể loại " + name + " đã được lưu thành công");

        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = service.get(id);
            List<Category> categories = service.listHierarchicalCategoriesInform();

            model.addAttribute("category", category);
            model.addAttribute("categories", categories);
            model.addAttribute("pageTitle", "Chỉnh sửa thể loại");

            return "categories/category_form";
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());

            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {
        service.updateCategoryEnabledStatus(id, enabled);
        String status = enabled ? " được kích hoạt" : " bị vô hiệu hoá";
        String message = "Thể loại có id là " + id + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            String categoryDir = "category-photos/" + id;
            FileUploadUtils.removeDir(categoryDir);

            redirectAttributes.addFlashAttribute("message", "Thể loại có id là " + id + " đã được xóa thành công");
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/categories";
    }
}
