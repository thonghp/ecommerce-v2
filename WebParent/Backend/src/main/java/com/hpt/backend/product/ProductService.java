package com.hpt.backend.product;

import com.hpt.common.entity.Product;
import com.hpt.common.exception.ProductNotFoundException;
import com.hpt.common.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static com.hpt.common.utils.CommonUtils.ASCENDING;

@Service
@Transactional
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
    public List<Product> listByPage(PageInfo pageInfo, int pageNum, String sortField, String sortType, String keyword,
                                    Integer categoryId) {
        Sort sort = Sort.by(sortField);
        sort = sortType.equals(ASCENDING) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);

        Page<Product> page = repo.findAll(pageable);

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page = repo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            }

            page = repo.search(keyword, pageable);
        }

        if (categoryId != null && categoryId > 0) {
            String categoryIdMatch = "-" + categoryId + "-";
            page = repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
        }


        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setTotalElements(page.getTotalElements());

        return page.getContent();
    }

    /**
     * Save product information. If the id does not exist, save the product, otherwise the id already exists,
     * update the product
     *
     * @param product product object to save
     * @return saved product object
     */
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }

        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().replaceAll(" ", "-");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "-"));
        }

        product.setUpdatedTime(new Date());

        return repo.save(product);
    }

    public void saveProductPrice(Product productInForm) {
        Product productInDB = repo.findById(productInForm.getId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());

        repo.save(productInDB);
    }

    /**
     * Check product name is unique
     *
     * @param id   id of the product
     * @param name name of the product
     * @return "Ok" if the product name is unique, otherwise return "Duplicated"
     */
    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Product productByName = repo.findByName(name);

        if (isCreatingNew) {
            if (productByName != null) return "Duplicated";
        } else {
            if (productByName != null && productByName.getId() != id) {
                return "Duplicated";
            }
        }

        return "OK";
    }

    /**
     * Update the enabled status of a product
     *
     * @param id      id of the product
     * @param enabled enabled status
     */
    public void updateProductEnabledStatus(Integer id, boolean enabled) {
        repo.updateEnabledStatus(id, enabled);
    }

    /**
     * Delete a product by id
     *
     * @param id id of the product you want to delete
     * @throws ProductNotFoundException if the product does not exist
     */
    public void delete(Integer id) throws ProductNotFoundException {
        Long countById = repo.countById(id);

        if (countById == null || countById == 0) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }

        repo.deleteById(id);
    }

    /**
     * Get a product by id
     *
     * @param id id of the product
     * @return product object corresponding to id
     * @throws ProductNotFoundException if the product does not exist
     */
    public Product get(Integer id) throws ProductNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (Exception ex) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
    }
}
