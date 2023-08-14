package com.hpt.backend.user.controller;

import com.hpt.backend.FileUploadUtils;
import com.hpt.backend.paging.PagingAndSortingHelper;
import com.hpt.backend.paging.PagingAndSortingParam;
import com.hpt.backend.user.UserService;
import com.hpt.backend.user.export.UserCsvExporter;
import com.hpt.backend.user.export.UserExcelExporter;
import com.hpt.backend.user.export.UserPdfExporter;
import com.hpt.common.entity.Role;
import com.hpt.common.entity.User;
import com.hpt.common.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    private String defaultRedirectURL = "redirect:/users/page/1?sortField=id&sortType=asc";

    @GetMapping("/users")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum,
                             @PagingAndSortingParam(listName = "users", moduleURL = "/users") PagingAndSortingHelper helper) {
        userService.listByPage(pageNum, helper);

        return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        User user = new User();
        user.setEnabled(true);
        List<Role> roles = userService.listRoles();

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("pageTitle", "Thêm nhân viên");

        return "users/user_form";
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

        return getRedirectURLToAffectedUser(user);
    }

    private String getRedirectURLToAffectedUser(User user) {
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortType=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.get(id);
            List<Role> roles = userService.listRoles();

            model.addAttribute("user", user);
            model.addAttribute("roles", roles);
            model.addAttribute("pageTitle", "Chỉnh sửa nhân viên");

            return "users/user_form";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());

            return defaultRedirectURL;
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            String categoryDir = "user-photos/" + id;
            FileUploadUtils.removeDir(categoryDir);

            redirectAttributes.addFlashAttribute("message", "Nhân viên có id là " + id + " đã được xóa thành công");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return defaultRedirectURL;
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        userService.updateUserEnabledStatus(id, enabled);
        String status = enabled ? " được kích hoạt" : " bị vô hiệu hoá";
        String message = "Nhân viên có id là " + id + status;
        redirectAttributes.addFlashAttribute("message", message);

        return defaultRedirectURL;
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();

        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(listUsers, response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();

        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(listUsers, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();

        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(listUsers, response);
    }
}
