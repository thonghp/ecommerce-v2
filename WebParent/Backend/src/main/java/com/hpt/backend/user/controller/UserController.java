package com.hpt.backend.user.controller;

import com.hpt.backend.FileUploadUtils;
import com.hpt.backend.user.UserService;
import com.hpt.common.entity.Role;
import com.hpt.common.entity.User;
import com.hpt.common.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "id", UserService.DEFAULT_SORT_TYPE, null);
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @RequestParam(name = "sortField") String sortField,
                             @RequestParam(name = "sortType") String sortType,
                             @RequestParam(name = "keyword", required = false) String keyword) {
        Page<User> page = userService.listByPage(pageNum, sortField, sortType, keyword);
        List<User> users = page.getContent();
        String reverseSortType = sortType.equals("asc") ? "desc" : "asc";

        long startCount = (long) (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
        long endCount = startCount + UserService.USERS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortType", sortType);
        model.addAttribute("reverseSortType", reverseSortType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("users", users);

        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        User user = new User();
        user.setEnabled(true);
        List<Role> roles = userService.listRoles();

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("pageTitle", "Thêm nhân viên");

        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            user.setImagePath(fileName);
            User savedUser = userService.save(user);

            String uploadDir = "user-photos/" + savedUser.getId();

            FileUploadUtils.cleanDir(uploadDir); // remove ảnh cũ trước khi lưu ảnh mới
            FileUploadUtils.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getImagePath().isEmpty()) user.setImagePath(null);
            userService.save(user);
        }

        String fullName = user.getLastName() + " " + user.getFirstName();
        redirectAttributes.addFlashAttribute("message", "Nhân viên " + fullName + " đã được lưu thành công");

        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.get(id);
            List<Role> roles = userService.listRoles();

            model.addAttribute("user", user);
            model.addAttribute("roles", roles);
            model.addAttribute("pageTitle", "Chỉnh sửa nhân viên");

            return "user_form";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());

            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Nhân viên có id là " + id + " đã được xóa thành công");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        userService.updateUserEnabledStatus(id, enabled);
        String status = enabled ? " được kích hoạt" : " bị vô hiệu hoá";
        String message = "Nhân viên có id là " + id + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";
    }
}
