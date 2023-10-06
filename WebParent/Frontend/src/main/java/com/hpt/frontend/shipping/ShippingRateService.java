package com.hpt.frontend.shipping;

import com.hpt.common.entity.Address;
import com.hpt.common.entity.Customer;
import com.hpt.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class ShippingRateService {
    @Autowired
    private ShippingRateRepository repo;

    /**
     * Get shipping rate according to customer information
     *
     * @param customer the customer to get shipping rate
     * @return the shipping rate
     */
    public ShippingRate getShippingRateForCustomer(Customer customer) {
        String state = customer.getState();
        if (state == null || state.isEmpty()) {
            state = customer.getCity();
        }

        return repo.findByCountryAndState(customer.getCountry(), state);
    }

    /**
     * Get shipping rate according to address information
     *
     * @param address the address to get shipping rate
     * @return the shipping rate
     */
    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();
        if (state == null || state.isEmpty()) {
            state = address.getCity();
        }

        return repo.findByCountryAndState(address.getCountry(), state);
    }
}
