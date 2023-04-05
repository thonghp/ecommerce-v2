package com.hpt.backend.user.controller;

import com.hpt.backend.user.UserService;
import com.hpt.common.entity.Role;
import com.hpt.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listAll(Model model) {
        List<User> users = userService.listAll();
        model.addAttribute("listUsers", users);

        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        User user = new User();
        user.setEnabled(true);
        List<Role> listRoles = userService.listRoles();
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);

        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes) {
        userService.save(user);
        String fullName = user.getLastName() + " " + user.getFirstName();
        redirectAttributes.addFlashAttribute("message", "Nhân viên " + fullName + " đã được lưu thành công");
        return "redirect:/users";
    }
}
