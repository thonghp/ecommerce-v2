package com.hpt.backend.product.controller;

import com.hpt.backend.product.ProductService;
import com.hpt.common.dto.ProductDTO;
import com.hpt.common.entity.product.Product;
import com.hpt.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductRestController {

    @Autowired
    private ProductService service;

    @PostMapping("/products/check_unique")
    public String checkUnique(@RequestParam(value = "id", required = false) Integer id, @RequestParam("name") String name) {
        return service.checkUnique(id, name);
    }

    @GetMapping("/products/get/{id}")
    public ProductDTO getProductInfo(@PathVariable Integer id) throws ProductNotFoundException {
        Product product = service.get(id);

        return new ProductDTO(product.getName(), product.getMainImagePath(),
                product.getDiscountPrice(), product.getCost());
    }
}
