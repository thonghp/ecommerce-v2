package com.hpt.backend.customer;

import com.hpt.backend.paging.PagingAndSortingHelper;
import com.hpt.backend.setting.country.CountryRepository;
import com.hpt.common.entity.Country;
import com.hpt.common.entity.Customer;
import com.hpt.common.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional

public class CustomerService {
    public static final int CUSTOMERS_PER_PAGE = 5;

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private CountryRepository countryRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Returns a paginated list of default or search customers and sorted ascending or descending by the specified column.
     *
     * @param pageNum The page requests to return data
     * @param helper  The helper to get the list of customers
     * @return a list of customers
     */
    public List<Customer> listByPage(int pageNum, PagingAndSortingHelper helper) {
        return (List<Customer>) helper.listByPage(pageNum, CUSTOMERS_PER_PAGE, customerRepo);
    }

    /**
     * Update the enabled status of a customer
     *
     * @param id      id of the customer
     * @param enabled enabled status
     */
    public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
        customerRepo.updateEnabledStatus(id, enabled);
    }

    /**
     * Get a customer by id
     *
     * @param id id of the customer
     * @return customer object corresponding to id
     * @throws CustomerNotFoundException if the customer does not exist
     */
    public Customer get(Integer id) throws CustomerNotFoundException {
        try {
            return customerRepo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new CustomerNotFoundException("Could not find any customers with ID " + id);
        }
    }

    /**
     * Returns all countries sorted by name in ascending order
     *
     * @return a list of countries
     */
    public List<Country> listAllCountries() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    /**
     * Check if the email already exists. The result is {@code true} if the input id is {@code null} and
     * the incoming email does not exist or the received id is not {@code null} and the received email has the same id
     * as the received id.
     *
     * @param email email to be checked
     * @return {@code true} if email is unique, {@code false} otherwise
     */
    public boolean isEmailUnique(Integer id, String email) {
        Customer existCustomer = customerRepo.findByEmail(email);

        if (existCustomer != null && existCustomer.getId() != id) {
            // found another customer having the same email
            return false;
        }

        return true;
    }

    /**
     * Save customer information. If the id does not exist, save the customer, otherwise the id already exists, update the customer
     *
     * @param customerInForm customer object to save
     */
    public void save(Customer customerInForm) {
        Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();

        if (!customerInForm.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
            customerInForm.setPassword(encodedPassword);
        } else {
            customerInForm.setPassword(customerInDB.getPassword());
        }

        customerInForm.setCreatedTime(customerInDB.getCreatedTime());
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());
        customerRepo.save(customerInForm);
    }

    /**
     * Delete a customer by id
     *
     * @param id id of the customer you want to delete
     * @throws CustomerNotFoundException if the customer does not exist
     */
    public void delete(Integer id) throws CustomerNotFoundException {
        Long count = customerRepo.countById(id);
        if (count == null || count == 0) {
            throw new CustomerNotFoundException("Could not find any customers with ID " + id);
        }

        customerRepo.deleteById(id);
    }
}
