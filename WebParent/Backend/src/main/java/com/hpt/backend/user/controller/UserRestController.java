package com.hpt.backend.user.controller;

import com.hpt.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {
    @Autowired
    private UserService userService;

    // @RequestParam(value = "id", required = false) Integer id
    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@RequestParam("email") String email) {
        return userService.isEmailUnique(email) ? "OK" : "Duplicated";
    }
}
