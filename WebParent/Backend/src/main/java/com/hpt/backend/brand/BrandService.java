package com.hpt.backend.brand;

import com.hpt.backend.paging.PagingAndSortingHelper;
import com.hpt.common.entity.Brand;
import com.hpt.common.exception.BrandNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class BrandService {
    @Autowired
    private BrandRepository repo;

    public final static int BRANDS_PER_PAGE = 5;

    /**
     * Returns a list of brands only with id and name
     *
     * @return a list of brands
     */
    public List<Brand> listAll() {
        return repo.findAll();
    }

    /**
     * Returns a paginated list of default or search brands and sorted ascending or descending by the specified column.
     *
     * @param pageNum The page requests to return data
     * @param helper  PagingAndSortingHelper object
     * @return a list of brands
     */
    public List<Brand> listByPage(int pageNum, PagingAndSortingHelper helper) {
        return (List<Brand>) helper.listByPage(pageNum, BRANDS_PER_PAGE, repo);
    }

    /**
     * Save brand information. If the id does not exist, save the brand, otherwise the id already exists, update the brand
     *
     * @param brand brand object to save
     * @return saved brand object
     */
    public Brand save(Brand brand) {
        return repo.save(brand);
    }

    /**
     * Get a brand by id
     *
     * @param id id of the brand
     * @return brand object corresponding to id
     * @throws BrandNotFoundException if the brand does not exist
     */
    public Brand get(Integer id) throws BrandNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (Exception ex) {
            throw new BrandNotFoundException("Could not find a brand with id " + id);
        }
    }

    /**
     * Update the enabled status of a brand
     *
     * @param id      id of the brand
     * @param enabled enabled status
     */
    public void updateBrandEnabledStatus(Integer id, boolean enabled) {
        repo.updateEnabledStatus(id, enabled);
    }

    /**
     * Delete a brand by id
     *
     * @param id id of the brand you want to delete
     * @throws BrandNotFoundException if the brand does not exist
     */
    public void delete(Integer id) throws BrandNotFoundException {
        Long countById = repo.countById(id);
        if (countById == null || countById == 0) {
            throw new BrandNotFoundException("Could not find a brand with id " + id);
        }
        repo.deleteById(id);
    }

    /**
     * Check brand name is unique
     *
     * @param id   id of the brand
     * @param name name of the brand
     * @return "Ok" if the brand name is unique, otherwise return "Duplicated"
     */
    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Brand brandByName = repo.findByName(name);

        if (isCreatingNew) {
            if (brandByName != null) return "Duplicated";
        } else {
            if (brandByName != null && brandByName.getId() != id) {
                return "Duplicated";
            }
        }

        return "OK";
    }
}
