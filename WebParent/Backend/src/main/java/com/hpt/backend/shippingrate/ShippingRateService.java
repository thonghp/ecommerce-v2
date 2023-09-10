package com.hpt.backend.shippingrate;

import com.hpt.backend.paging.PagingAndSortingHelper;
import com.hpt.backend.setting.country.CountryRepository;
import com.hpt.common.entity.Country;
import com.hpt.common.entity.ShippingRate;
import com.hpt.common.exception.ShippingRateAlreadyExistsException;
import com.hpt.common.exception.ShippingRateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ShippingRateService {
    public static final int RATES_PER_PAGE = 5;
    @Autowired
    private ShippingRateRepository shipRepo;
    @Autowired
    private CountryRepository countryRepo;

    /**
     * Returns a paginated list of default or search shipping rate and sorted ascending or descending by the specified column.
     *
     * @param pageNum The page requests to return data
     * @param helper  PagingAndSortingHelper object
     * @return a list of shipping rate
     */
    public List<ShippingRate> listByPage(int pageNum, PagingAndSortingHelper helper) {
        return (List<ShippingRate>) helper.listByPage(pageNum, RATES_PER_PAGE, shipRepo);
    }

    /**
     * Returns a list of countries sorted by name
     *
     * @return a list of countries
     */
    public List<Country> listAllCountries() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    /**
     * Save shipping rate information. If the id does not exist, save the shipping rate, otherwise the id already exists,
     * update the shipping rate
     *
     * @param rateInForm shipping rate object to save
     * @throws ShippingRateAlreadyExistsException if the shipping rate already exists
     */
    public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
        ShippingRate rateInDB = shipRepo.findByCountryAndState(
                rateInForm.getCountry().getId(), rateInForm.getState());
        boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
        boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.equals(rateInForm);

        if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
            throw new ShippingRateAlreadyExistsException("Đã có phí ship cho khu vực "
                    + rateInForm.getCountry().getName() + ", " + rateInForm.getState());
        }
        shipRepo.save(rateInForm);
    }

    /**
     * Get a shipping rate by id
     *
     * @param id id of the shipping rate
     * @return shipping rate object corresponding to id
     * @throws ShippingRateNotFoundException if the shipping rate does not exist
     */
    public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
        try {
            return shipRepo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }
    }

    /**
     * Update the COD support status of a shipping rate
     *
     * @param id           id of the shipping rate
     * @param codSupported COD support status
     * @throws ShippingRateNotFoundException if the shipping rate does not exist
     */
    public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
        Long count = shipRepo.countById(id);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }

        shipRepo.updateCODSupport(id, codSupported);
    }

    /**
     * Delete a shipping rate by id
     *
     * @param id id of the shipping rate
     * @throws ShippingRateNotFoundException if the shipping rate does not exist
     */
    public void delete(Integer id) throws ShippingRateNotFoundException {
        Long count = shipRepo.countById(id);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);

        }
        shipRepo.deleteById(id);
    }
}
