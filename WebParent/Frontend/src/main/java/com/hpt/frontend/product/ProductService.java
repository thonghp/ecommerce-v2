package com.hpt.frontend.product;

import com.hpt.common.entity.product.Product;
import com.hpt.common.exception.ProductNotFoundException;
import com.hpt.common.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 12;

    @Autowired
    private ProductRepository repo;

    /**
     * Get a list of products by category
     *
     * @param pageInfo   The page info object
     * @param pageNum    The current page number
     * @param categoryId The category ID
     * @return List of products
     */
    public List<Product> listByCategory(PageInfo pageInfo, int pageNum, Integer categoryId) {
        String categoryIdMatch = "-" + categoryId + "-";
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
        Page<Product> page = repo.listByCategory(categoryId, categoryIdMatch, pageable);
        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setTotalElements(page.getTotalElements());

        return page.getContent();
    }

    /**
     * Get a product by alias
     *
     * @param alias The alias of the product
     * @return The product
     * @throws ProductNotFoundException If the product is not found
     */
    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = repo.findByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Could not find any product with alias " + alias);
        }

        return product;
    }

    /**
     * Search for products by keyword
     *
     * @param pageInfo The page info object
     * @param keyword  The keyword to search for
     * @param pageNum  The current page number
     * @return List of products
     */
    public List<Product> search(PageInfo pageInfo, String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
        Page<Product> page = repo.search(keyword, pageable);
        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setTotalElements(page.getTotalElements());

        return page.getContent();
    }
}
