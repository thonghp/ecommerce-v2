package com.hpt.common.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public class AbstractAddressWithCountry extends AbstractAddress {
    @ManyToOne
    @JoinColumn(name = "country_id")
    protected Country country;

    @Override
    public String toString() {
        String address = "";

        if (!addressLine1.isEmpty()) address += addressLine1;

        if (addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;

        if (!city.isEmpty()) address += ", " + city;

        if (state != null && !state.isEmpty()) address += ", " + state;

        address += ", " + country.getName();

        if (!postalCode.isEmpty()) address += ". Mã bưu điện: " + postalCode;

        return address;
    }
}
