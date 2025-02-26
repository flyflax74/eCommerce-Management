package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.annotation.PagingAndSortingParam;
import com.ecommerce.site.admin.exception.UserNotFoundException;
import com.ecommerce.site.admin.export.UserCsvExporter;
import com.ecommerce.site.admin.export.UserExcelExporter;
import com.ecommerce.site.admin.export.UserPdfExporter;
import com.ecommerce.site.admin.helper.PagingAndSortingHelper;
import com.ecommerce.site.admin.model.entity.Role;
import com.ecommerce.site.admin.model.entity.User;
import com.ecommerce.site.admin.security.UserDetailsImpl;
import com.ecommerce.site.admin.service.UserService;
import com.ecommerce.site.admin.util.AmazonS3Util;
import com.ecommerce.site.admin.util.FileUploadUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import static com.ecommerce.site.admin.constant.AmazonS3Constant.S3_SERVICE_ACTIVE;


@Controller
public class UserController {

    private final String defaultRedirectUrl = "redirect:/users/offset/5/page/1?sortField=email&sortDir=asc";

    @Autowired
    private UserService service;

    @GetMapping("/users")
    public String listAll() {
        return defaultRedirectUrl;
    }

    @GetMapping("/users/offset/{pageSize}/page/{pageNumber}")
    public String listByPage(@PagingAndSortingParam(listName = "listUsers", moduleUrl = "/users") PagingAndSortingHelper helper,
                             @PathVariable("pageSize") int pageSize, @PathVariable("pageNumber") int pageNumber) {
        service.listByPage(pageNumber, pageSize, helper);
        return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(@NotNull Model model) {
        List<Role> listRoles = service.listRoles();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Create New User");
        model.addAttribute("listRoles", listRoles);
        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, @NotNull RedirectAttributes attributes,
                           @RequestParam("fileImage") @NotNull MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            user.setPhotos(fileName);
            User savedUser = service.save(user);

            saveUserPhotosImage(multipartFile, fileName, savedUser);
        } else {
            if (user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
            service.save(user);
        }
        attributes.addFlashAttribute("message", "The user has been saved successfully");
        return getRedirectUrlToAffectedUser(user);
    }

    private void saveUserPhotosImage(@RequestParam("image") @NotNull MultipartFile multipartFile, String fileName, User user) throws IOException {
        if (S3_SERVICE_ACTIVE) {
            String uploadS3Dir = "user-photos/" + user.getId();
            AmazonS3Util.removeFolder(uploadS3Dir);
            AmazonS3Util.uploadFile(uploadS3Dir, fileName, multipartFile.getInputStream());
        } else {
            String uploadDir = "../user-photos/" + user.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    private @NotNull String getRedirectUrlToAffectedUser(@NotNull User user) {
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return String.format("redirect:/users/page/1?sortField=id&sortDir=asc&keyword=%s", firstPartOfEmail);
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Integer id, @NotNull Model model, RedirectAttributes attributes) {
        try {
            User user = service.findById(id);
            List<Role> listRoles = service.listRoles();

            model.addAttribute("user", user);
            model.addAttribute("pageTitle", String.format("Edit User (ID %s)", id));
            model.addAttribute("listRoles", listRoles);

            return "users/user_form";
        } catch (UserNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, @NotNull RedirectAttributes attributes, @NotNull HttpServletRequest request) {
        try {
            User user = service.findById(id);
            if (!user.hasRole("Admin")) {
                if (user.getPhotos() != null) {
                    if (S3_SERVICE_ACTIVE) {
                        AmazonS3Util.removeFolder("user-photos/" + id);
                    } else {
                        FileUploadUtil.removeDir("../user-photos/" + id);
                    }
                }
                service.deleteById(id);
                attributes.addFlashAttribute("message", String.format("The user ID %s has been deleted successfully", id));
            } else {
                attributes.addFlashAttribute("message", "The admin user cannot be deleted");
            }
        } catch (UserNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
        }
        return String.format("redirect:%s", request.getHeader("Referer"));
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                                          @NotNull RedirectAttributes attributes, @NotNull HttpServletRequest request) throws UserNotFoundException {
        User user = service.findById(id);

        if (!user.hasRole("Admin")) {
            service.updateEnabledStatus(id, enabled);
            attributes.addFlashAttribute("message", String.format("The user ID %s has been %s", id, enabled ? "enabled" : "disabled"));
        } else {
            attributes.addFlashAttribute("message", "The admin user cannot be disabled");
        }
        return String.format("redirect:%s", request.getHeader("Referer"));
    }

    @GetMapping("/users/detail/{id}")
    public String viewUser(@PathVariable("id") Integer id, @NotNull Model model, RedirectAttributes attributes) {
        try {
            User user = service.findById(id);
            model.addAttribute("user", user);
            return "users/user_detail_modal";
        } catch (UserNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectUrl;
        }
    }

    @GetMapping("/users/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAll();
        UserCsvExporter userCsvExporter = new UserCsvExporter();
        userCsvExporter.export(listUsers, response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAll();
        UserExcelExporter userExcelExporter = new UserExcelExporter();
        userExcelExporter.export(listUsers, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAll();
        UserPdfExporter userPdfExporter = new UserPdfExporter();
        userPdfExporter.export(listUsers, response);
    }

    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal @NotNull UserDetailsImpl loggedUser, @NotNull Model model) {
        String email = loggedUser.getUsername();
        User user = service.findByEmail(email);
        model.addAttribute("user", user);
        return "users/account_form";
    }

    @PostMapping("/account/update")
    public String saveDetails(User user, RedirectAttributes attributes, @AuthenticationPrincipal UserDetailsImpl loggedUser,
                              @RequestParam("image") @NotNull MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            user.setPhotos(fileName);
            User savedUser = service.updateAccount(user);

            saveUserPhotosImage(multipartFile, fileName, savedUser);
        } else {
            if (user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
            service.updateAccount(user);
        }
        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setLastName(user.getLastName());
        attributes.addFlashAttribute("message", "Your account details have been updated");
        return "redirect:/account";
    }
}
