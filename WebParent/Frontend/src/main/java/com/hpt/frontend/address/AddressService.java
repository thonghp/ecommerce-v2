package com.hpt.frontend.address;

import com.hpt.common.entity.Address;
import com.hpt.common.entity.Customer;
import com.hpt.common.exception.AddressNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class AddressService {
    @Autowired
    private AddressRepository repo;

    /**
     * Get list of addresses of a customer
     *
     * @param customer the customer
     * @return a list of addresses
     */
    public List<Address> listAddressBook(Customer customer) {
        return repo.findByCustomer(customer);
    }

    /**
     * Save an address of the customer
     *
     * @param address the address of the customer
     */
    public void save(Address address) {
        repo.save(address);
    }

    /**
     * Get an address of the customer
     *
     * @param addressId  the id of the address
     * @param customerId the id of the customer
     * @return an address
     */
    public Address get(Integer addressId, Integer customerId) throws AddressNotFoundException {
        try {
            return repo.findByIdAndCustomer(addressId, customerId);
        } catch (Exception ex) {
            throw new AddressNotFoundException("Could not find an address with id " + addressId);
        }
    }

    /**
     * Delete an address of the customer
     *
     * @param addressId  the id of the address
     * @param customerId the id of the customer
     */
    public void delete(Integer addressId, Integer customerId) {
        repo.deleteByIdAndCustomer(addressId, customerId);
    }

    /**
     * Set an address as default address of the customer.
     *
     * @param defaultAddressId the id of the address
     * @param customerId       the id of the customer
     */
    public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
        if (defaultAddressId > 0) {
            repo.setDefaultAddress(defaultAddressId);
        }

        repo.setNonDefaultForOthers(defaultAddressId, customerId);
    }
}
