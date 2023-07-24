package com.hpt.frontend.product;

import com.hpt.common.entity.Product;
import com.hpt.common.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 10;

    @Autowired
    private ProductRepository repo;

    public List<Product> listByCategory(PageInfo pageInfo, int pageNum, Integer categoryId) {
        String categoryIdMatch = "-" + categoryId + "-";
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
        Page<Product> page = repo.listByCategory(categoryId, categoryIdMatch, pageable);
        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setTotalElements(page.getTotalElements());

        return page.getContent();
    }
}
