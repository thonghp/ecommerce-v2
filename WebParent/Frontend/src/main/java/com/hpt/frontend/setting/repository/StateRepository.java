package com.hpt.frontend.setting.repository;

import com.hpt.common.entity.Country;
import com.hpt.common.entity.State;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StateRepository extends CrudRepository<State, Integer> {
    List<State> findByCountryOrderByNameAsc(Country country);
}
