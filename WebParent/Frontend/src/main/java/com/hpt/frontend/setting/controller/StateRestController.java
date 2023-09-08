package com.hpt.frontend.setting.controller;

import com.hpt.common.dto.StateDTO;
import com.hpt.common.entity.Country;
import com.hpt.common.entity.State;
import com.hpt.frontend.setting.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StateRestController {
    @Autowired
    private StateRepository repo;

    @GetMapping("/settings/list_states_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {
        Country country = new Country();
        country.setId(countryId);
        List<State> listStates = repo.findByCountryOrderByNameAsc(country);
        List<StateDTO> result = new ArrayList<>();

        for (State state : listStates) {
            result.add(new StateDTO(state.getId(), state.getName()));
        }

        return result;
    }
}
