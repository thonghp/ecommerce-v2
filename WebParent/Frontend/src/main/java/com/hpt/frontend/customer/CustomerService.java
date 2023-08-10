package com.hpt.frontend.customer;

import com.hpt.common.entity.Country;
import com.hpt.common.entity.Customer;
import com.hpt.frontend.setting.repository.CountryRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CustomerService {
    @Autowired
    private CountryRepository countryRepo;
    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Return a list of all countries sorted by name in ascending order
     *
     * @return a list of countries
     */
    public List<Country> listAllCountries() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    /**
     * Check customer email is unique or not
     *
     * @param email customer email to be checked
     * @return true if email is unique, otherwise false
     */
    public boolean isEmailUnique(String email) {
        Customer customer = customerRepo.findByEmail(email);
        return customer == null;
    }

    /**
     * Register a new customer account to the database
     *
     * @param customer customer to be registered
     */
    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);

        customerRepo.save(customer);
    }

    /**
     * Encode customer password
     *
     * @param customer customer to be encoded
     */
    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    /**
     * Verify customer account
     *
     * @param verificationCode verification code
     * @return true if verification is successful, otherwise false
     */
    public boolean verify(String verificationCode) {
        Customer customer = customerRepo.findByVerificationCode(verificationCode);

        if (customer == null || customer.isEnabled()) {
            return false;
        } else {
            customerRepo.enable(customer.getId());
            return true;
        }
    }
}
