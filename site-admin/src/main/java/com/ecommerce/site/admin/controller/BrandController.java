package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.annotation.PagingAndSortingParam;
import com.ecommerce.site.admin.exception.BrandNotFoundException;
import com.ecommerce.site.admin.helper.PagingAndSortingHelper;
import com.ecommerce.site.admin.model.entity.Brand;
import com.ecommerce.site.admin.model.entity.Category;
import com.ecommerce.site.admin.service.BrandService;
import com.ecommerce.site.admin.service.CategoryService;
import com.ecommerce.site.admin.util.AmazonS3Util;
import com.ecommerce.site.admin.util.FileUploadUtil;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/brands")
public class BrandController {

    private final String defaultRedirectUrl = "redirect:/brands/offset/5/page/1?sortField=name&sortDir=asc";

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public String listFirstPage() {
        return defaultRedirectUrl;
    }

    @GetMapping("/offset/{pageSize}/page/{pageNumber}")
    public String listByPage(@PagingAndSortingParam(listName = "listBrands", moduleUrl = "/brands") PagingAndSortingHelper helper,
                             @PathVariable("pageSize") int pageSize, @PathVariable("pageNumber") int pageNumber) {
        brandService.listByPage(pageNumber, pageSize, helper);
        return "brands/brands";
    }

    @GetMapping("/new")
    public String newBrand(@NotNull Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("brand", new Brand());
        model.addAttribute("pageTitle", "Create New Brand");

        return "brands/brand_form";
    }

    @PostMapping("/save")
    public String saveBrand(@NotNull @RequestParam("fileImage") MultipartFile multipartFile,
                            Brand brand, RedirectAttributes attributes) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            brand.setLogo(fileName);
            Brand savedBrand = brandService.save(brand);

            if (S3_SERVICE_ACTIVE) {
                String uploadS3Dir = "brand-logos/" + savedBrand.getId();
                AmazonS3Util.removeFolder(uploadS3Dir);
                AmazonS3Util.uploadFile(uploadS3Dir, fileName, multipartFile.getInputStream());
            } else {
                String uploadDir = "../brand-logos/" + savedBrand.getId();
                FileUploadUtil.cleanDir(uploadDir);
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        } else {
            brandService.save(brand);
            attributes.addFlashAttribute("message", "The brand has been saved successfully");
        }
        return String.format("redirect:/brands/page/1?sortField=id&sortDir=asc&keyword=%s", brand.getName());
    }

    @GetMapping("/edit/{id}")
    public String editBrand(@PathVariable("id") Integer id, @NotNull Model model, RedirectAttributes attributes) {
        try {
            Brand brand = brandService.get(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", String.format("Edit Brand (ID: %s)", id));

            return "brands/brand_form";
        } catch (BrandNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectUrl;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable("id") Integer id, @NotNull RedirectAttributes attributes, @NotNull HttpServletRequest request) {
        try {
            brandService.delete(id);
            if (S3_SERVICE_ACTIVE) {
                AmazonS3Util.removeFolder("brand-logos/" + id);
            } else {
                FileUploadUtil.removeDir("../brand-logos/" + id);
            }
            attributes.addFlashAttribute("message", String.format("The brand ID %s has been deleted successfully", id));
        } catch (BrandNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
        }
        return String.format("redirect:%s", request.getHeader("Referer"));
    }
}
