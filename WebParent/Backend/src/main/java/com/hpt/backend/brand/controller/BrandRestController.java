package com.hpt.backend.brand.controller;

import com.hpt.backend.brand.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {
    @Autowired
    private BrandService service;

    @PostMapping("/brands/check_unique")
    public String checkUnique(@RequestParam(value = "id", required = false) Integer id,
                              @RequestParam("name") String name) {
        return service.checkUnique(id, name);
    }
}
