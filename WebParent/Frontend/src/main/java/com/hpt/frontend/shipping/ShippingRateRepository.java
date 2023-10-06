package com.hpt.frontend.shipping;

import com.hpt.common.entity.Country;
import com.hpt.common.entity.ShippingRate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {
    ShippingRate findByCountryAndState(Country country, String state);
}
