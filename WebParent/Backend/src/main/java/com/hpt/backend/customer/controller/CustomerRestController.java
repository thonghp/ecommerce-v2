package com.hpt.backend.customer.controller;

import com.hpt.backend.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {
    @Autowired
    private CustomerService service;

    @PostMapping("/customers/check_email")
    public String checkDuplicateEmail(@RequestParam(value = "id", required = false) Integer id, @RequestParam("email") String email) {
        return service.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
