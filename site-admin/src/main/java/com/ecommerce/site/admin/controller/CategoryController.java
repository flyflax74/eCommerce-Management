package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.util.AmazonS3Util;
import com.ecommerce.site.admin.util.CategoryPageUtil;
import com.ecommerce.site.admin.exception.CategoryNotFoundException;
import com.ecommerce.site.admin.model.entity.Category;
import com.ecommerce.site.admin.export.CategoryCsvExporter;
import com.ecommerce.site.admin.service.CategoryService;
import com.ecommerce.site.admin.util.FileUploadUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.ecommerce.site.admin.constant.AmazonS3Constant.S3_SERVICE_ACTIVE;


@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping("")
    public String listFirstPage(String sortDir, Model model) {
        return listByPage(1, 1, sortDir, null, model);
    }

    @GetMapping("/offset/{pageSize}/page/{pageNumber}")
    public String listByPage(@PathVariable("pageSize") int pageSize, @PathVariable("pageNumber") int pageNumber,
                             String sortDir, String keyword, @NotNull Model model) {
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("pageNumber", pageNumber);
        if (sortDir == null || sortDir.isEmpty()) {
            sortDir = "asc";
        }

        CategoryPageUtil categoryPage = new CategoryPageUtil();
        List<Category> listCategories = service.listByPage(categoryPage, pageNumber, pageSize, sortDir, keyword);

        long startPage = (long) (pageNumber - 1) * pageSize + 1;
        long endPage = startPage + pageSize - 1;
        if (endPage > categoryPage.getTotalElements()) {
            endPage = categoryPage.getTotalElements();
        }

        String reverseSortDir = "asc".equals(sortDir) ? "desc" : "asc";

        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("moduleUrl", "/categories");

        return "categories/categories";
    }

    @GetMapping("/new")
    public String newCategory(@NotNull Model model) {
        List<Category> listCategories = service.listCategoriesUsedInForm();

        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");

        return "categories/category_form";
    }

    @PostMapping("/save")
    public String saveCategory(Category category, @RequestParam("fileImage") @NotNull MultipartFile multipartFile, RedirectAttributes attributes) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            category.setImage(fileName);
            Category savedCategory = service.save(category);

            if (S3_SERVICE_ACTIVE) {
                String uploadS3Dir = "category-images/" + savedCategory.getId();
                AmazonS3Util.removeFolder(uploadS3Dir);
                AmazonS3Util.uploadFile(uploadS3Dir, fileName, multipartFile.getInputStream());
            } else {
                String uploadDir = "../category-images/" + savedCategory.getId();
                FileUploadUtil.cleanDir(uploadDir);
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        } else {
            service.save(category);
        }
        attributes.addFlashAttribute("message", "The category has been saved successfully");
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Integer id, @NotNull Model model, RedirectAttributes attributes) {
        try {
            Category category = service.get(id);
            List<Category> listCategories = service.listCategoriesUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", String.format("Edit Category (ID: %s)", id));

            return "categories/category_form";
        } catch (CategoryNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/{id}/enabled/{status}")
    public String updateEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                                      @NotNull RedirectAttributes attributes, @NotNull HttpServletRequest request) {
        service.updateEnabledStatus(id, enabled);
        attributes.addFlashAttribute("message", String.format("The category ID %s has been %s", id, enabled ? "enabled" : "disabled"));
        return String.format("redirect:%s", request.getHeader("Referer"));
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, @NotNull RedirectAttributes attributes, @NotNull HttpServletRequest request) {
        try {
            service.delete(id);

            if (S3_SERVICE_ACTIVE) {
                AmazonS3Util.removeFolder("category-images/" + id);
            } else {
                FileUploadUtil.removeDir("../category-images/" + id);
            }
            attributes.addFlashAttribute("message", String.format("The category ID %s has been deleted successfully", id));
        } catch (CategoryNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
        }
        return String.format("redirect:%s", request.getHeader("Referer"));
    }

    @GetMapping("/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<Category> listCategories = service.listCategoriesUsedInForm();
        CategoryCsvExporter exporter = new CategoryCsvExporter();
        exporter.export(listCategories, response);
    }
}
