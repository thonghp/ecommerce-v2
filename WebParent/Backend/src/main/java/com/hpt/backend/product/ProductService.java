package com.hpt.backend.product;

import com.hpt.common.entity.Product;
import com.hpt.common.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hpt.common.utils.CommonUtils.ASCENDING;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repo;
    public final static int PRODUCTS_PER_PAGE = 5;

    /**
     * Returns a paginated list of default or search products and sorted ascending or descending by the specified column.
     *
     * @param pageInfo  object containing pagination information
     * @param pageNum   The page requests to return data
     * @param sortField The field to sort
     * @param sortType  The type of sort
     * @param keyword   The keyword to search
     * @return a list of products
     */
    public List<Product> listByPage(PageInfo pageInfo, int pageNum, String sortField, String sortType, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortType.equals(ASCENDING) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);
        Page<Product> page = repo.findAll(pageable);

        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setTotalElements(page.getTotalElements());

        return page.getContent();
    }
}
