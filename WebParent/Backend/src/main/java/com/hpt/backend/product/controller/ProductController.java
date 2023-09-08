package com.hpt.backend.product.controller;

import com.hpt.backend.FileUploadUtils;
import com.hpt.backend.brand.BrandService;
import com.hpt.backend.category.CategoryService;
import com.hpt.backend.product.ProductService;
import com.hpt.backend.security.WebUserDetails;
import com.hpt.common.entity.Brand;
import com.hpt.common.entity.Category;
import com.hpt.common.entity.Product;
import com.hpt.common.exception.ProductNotFoundException;
import com.hpt.common.utils.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

import static com.hpt.backend.product.ProductSaveHelper.*;
import static com.hpt.common.utils.CommonUtils.ASCENDING;
import static com.hpt.common.utils.CommonUtils.DESCENDING;

@Controller
public class ProductController {
    @Autowired
    private ProductService service;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    private final static String SORT_FIELD_NAME = "id";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return listByPage(1, model, SORT_FIELD_NAME, ASCENDING, null, 0);
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @RequestParam(name = "sortField") String sortField,
                             @RequestParam(name = "sortType") String sortType,
                             @RequestParam(name = "keyword", required = false) String keyword,
                             @RequestParam(name = "categoryId") Integer categoryId) {
        PageInfo pageInfo = new PageInfo();
        List<Product> products = service.listByPage(pageInfo, pageNum, sortField, sortType, keyword, categoryId);
        List<Category> listCategories = categoryService.listHierarchicalCategoriesInform();
        String reverseSortType = sortType.equals(ASCENDING) ? DESCENDING : ASCENDING;

        long startCount = pageInfo.getStartCount(pageNum, ProductService.PRODUCTS_PER_PAGE);
        long endCount = pageInfo.getEndCount(pageNum, ProductService.PRODUCTS_PER_PAGE);

        if (categoryId != null)
            model.addAttribute("categoryId", categoryId);

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
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("moduleURL", "/products");

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
        model.addAttribute("numberOfExistingExtraImages", 0);

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam(value = "image", required = false) MultipartFile mainImageMultipart,
                              @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal WebUserDetails loggedUser) throws IOException {
        if (loggedUser.hasRole("Salesperson")) {
            service.saveProductPrice(product);
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được lưu thành công.");
            return "redirect:/products";
        }

        setMainImageName(mainImageMultipart, product);
        setExistingExtraImageNames(imageIDs, imageNames, product);
        setNewExtraImageNames(extraImageMultiparts, product);
        setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = service.save(product);

        saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        deleteExtraImagesWeredRemovedOnForm(product);

        redirectAttributes.addFlashAttribute("message", savedProduct.getName() + " đã được lưu thành công.");

        return "redirect:/products";
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateProductEnabledStatus(@PathVariable("id") Integer id,
                                             @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        service.updateProductEnabledStatus(id, enabled);
        String status = enabled ? " được kích hoạt" : " bị vô hiệu hoá";
        String message = "Sản phẩm có id là " + id + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            String productExtraImagesDir = "product-photos/" + id + "/extras";
            String productImagesDir = "product-photos/" + id;

            FileUploadUtils.removeDir(productExtraImagesDir);
            FileUploadUtils.removeDir(productImagesDir);

            redirectAttributes.addFlashAttribute("message", "Sản phẩm có id là " + id + " đã được xóa thành công");
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model,
                              RedirectAttributes ra, @AuthenticationPrincipal WebUserDetails loggedUser) {
        try {
            Product product = service.get(id);
            List<Brand> listBrands = brandService.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();

            boolean isReadOnlyForSalesperson = false;

            if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
                if (loggedUser.hasRole("Salesperson")) {
                    isReadOnlyForSalesperson = true;
                }
            }

            model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "products/product_form";
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());

            return "redirect:/products";
        }
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model,
                                     RedirectAttributes ra) {
        try {
            Product product = service.get(id);
            model.addAttribute("product", product);

            return "products/product_detail_modal";

        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());

            return "redirect:/products";
        }
    }
}
