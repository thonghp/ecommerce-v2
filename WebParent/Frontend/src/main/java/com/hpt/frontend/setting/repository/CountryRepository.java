package com.hpt.frontend.setting.repository;

import com.hpt.common.entity.Country;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountryRepository extends CrudRepository<Country, Integer> {
    List<Country> findAllByOrderByNameAsc();

    Country findByCode(String countryCode);
}
