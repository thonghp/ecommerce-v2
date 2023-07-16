package com.hpt.backend.product.controller;

import com.hpt.backend.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

    @Autowired
    private ProductService service;

    @PostMapping("/products/check_unique")
    public String checkUnique(@RequestParam(value = "id", required = false) Integer id, @RequestParam("name") String name) {
        return service.checkUnique(id, name);
    }
}
