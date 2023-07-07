package com.hpt.backend.brand.controller;

import com.hpt.backend.FileUploadUtils;
import com.hpt.backend.brand.BrandService;
import com.hpt.backend.category.CategoryService;
import com.hpt.common.entity.Brand;
import com.hpt.common.entity.Category;
import com.hpt.common.exception.BrandNotFoundException;
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

import static com.hpt.common.utils.CommonUtils.ASCENDING;
import static com.hpt.common.utils.CommonUtils.DESCENDING;

@Controller
public class BrandController {
    @Autowired
    private BrandService service;
    @Autowired
    private CategoryService categoryService;

    private final static String SORT_FIELD_NAME = "id";

    @GetMapping("/brands")
    public String listFirstPage(Model model) {
        return listByPage(1, model, SORT_FIELD_NAME, ASCENDING, null);
    }

    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @RequestParam(name = "sortField") String sortField,
                             @RequestParam(name = "sortType") String sortType,
                             @RequestParam(name = "keyword", required = false) String keyword) {
        PageInfo pageInfo = new PageInfo();
        List<Brand> brands = service.listByPage(pageInfo, pageNum, sortField, sortType, keyword);
        String reverseSortType = sortType.equals(ASCENDING) ? DESCENDING : ASCENDING;

        long startCount = pageInfo.getStartCount(pageNum, BrandService.BRANDS_PER_PAGE);
        long endCount = pageInfo.getEndCount(pageNum, BrandService.BRANDS_PER_PAGE);

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortType", sortType);
        model.addAttribute("reverseSortType", reverseSortType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("brands", brands);

        return "brands/brands";
    }

    @GetMapping("/brands/new")
    public String newBrand(Model model) {
        Brand brand = new Brand();
        brand.setEnabled(true);
        List<Category> listCategories = categoryService.listHierarchicalCategoriesInform();

        model.addAttribute("brand", brand);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Thêm thương hiệu");

        return "brands/brand_form";
    }

    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, RedirectAttributes redirectAttributes,
                            @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            brand.setImagePath(fileName);
            Brand savedBrand = service.save(brand);

            String uploadDir = "brand-photos/" + savedBrand.getId();

            FileUploadUtils.cleanDir(uploadDir); // remove ảnh cũ trước khi lưu ảnh mới
            FileUploadUtils.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (brand.getImagePath().isEmpty()) brand.setImagePath(null);
            service.save(brand);
        }

        String name = brand.getName();
        redirectAttributes.addFlashAttribute("message", "Thương hiệu " + name + " đã được lưu thành công");

        return "redirect:/brands/page/1?sortField=id&sortType=asc&keyword=" + name;
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Brand brand = service.get(id);
            List<Category> listCategories = categoryService.listHierarchicalCategoriesInform();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Chỉnh sửa thương hiệu");

            return "brands/brand_form";
        } catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());

            return "redirect:/brands";
        }
    }

    @GetMapping("/brands/{id}/enabled/{status}")
    public String updateBrandEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                                           RedirectAttributes redirectAttributes) {
        service.updateBrandEnabledStatus(id, enabled);
        String status = enabled ? " được kích hoạt" : " bị vô hiệu hoá";
        String message = "Thương hiệu có id là " + id + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/brands";
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            String categoryDir = "brand-photos/" + id;
            FileUploadUtils.removeDir(categoryDir);

            redirectAttributes.addFlashAttribute("message", "Thương hiệu có id là " + id + " đã được xóa thành công");
        } catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/brands";
    }
}
