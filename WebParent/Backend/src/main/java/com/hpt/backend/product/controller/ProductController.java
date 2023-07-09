package com.hpt.backend.product.controller;

import com.hpt.backend.brand.BrandService;
import com.hpt.backend.product.ProductService;
import com.hpt.common.entity.Brand;
import com.hpt.common.entity.Product;
import com.hpt.common.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.hpt.common.utils.CommonUtils.ASCENDING;
import static com.hpt.common.utils.CommonUtils.DESCENDING;

@Controller
public class ProductController {
    @Autowired
    private ProductService service;
    @Autowired
    private BrandService brandService;
    private final static String SORT_FIELD_NAME = "id";

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return listByPage(1, model, SORT_FIELD_NAME, ASCENDING, null);
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @RequestParam(name = "sortField") String sortField,
                             @RequestParam(name = "sortType") String sortType,
                             @RequestParam(name = "keyword", required = false) String keyword) {
        PageInfo pageInfo = new PageInfo();
        List<Product> products = service.listByPage(pageInfo, pageNum, sortField, sortType, keyword);
        String reverseSortType = sortType.equals(ASCENDING) ? DESCENDING : ASCENDING;

        long startCount = pageInfo.getStartCount(pageNum, ProductService.PRODUCTS_PER_PAGE);
        long endCount = pageInfo.getEndCount(pageNum, ProductService.PRODUCTS_PER_PAGE);

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortType", sortType);
        model.addAttribute("reverseSortType", reverseSortType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("products", products);

        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> listBrands = brandService.listAll();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Thêm sản phẩm");

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes) {
        service.save(product);
        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");

        return "redirect:/products";
    }
}
