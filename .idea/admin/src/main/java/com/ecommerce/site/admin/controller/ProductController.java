package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.exception.ProductNotFoundException;
import com.ecommerce.site.admin.model.entity.Brand;
import com.ecommerce.site.admin.model.entity.Category;
import com.ecommerce.site.admin.model.entity.Product;
import com.ecommerce.site.admin.helper.PagingAndSortingHelper;
import com.ecommerce.site.admin.helper.ProductSaveHelper;
import com.ecommerce.site.admin.annotation.PagingAndSortingParam;
import com.ecommerce.site.admin.security.UserDetailsImpl;
import com.ecommerce.site.admin.service.BrandService;
import com.ecommerce.site.admin.service.CategoryService;
import com.ecommerce.site.admin.service.ProductService;
import com.ecommerce.site.admin.util.AmazonS3Util;
import com.ecommerce.site.admin.util.FileUploadUtil;
import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

import static com.ecommerce.site.admin.constant.AmazonS3Constant.S3_SERVICE_ACTIVE;


@Controller
@RequestMapping("/products")
public class ProductController {

    private final String defaultRedirectUrl = "redirect:/products/offset/5/page/1?sortField=name&sortDir=asc&categoryId=0";

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public String listFirstPage() {
        return defaultRedirectUrl;
    }

    @GetMapping("/offset/{pageSize}/page/{pageNumber}")
    public String listByPage(@PagingAndSortingParam(listName = "listProducts", moduleUrl = "/products") PagingAndSortingHelper helper,
                             @PathVariable("pageNumber") int pageNumber, @PathVariable("pageSize") int pageSize, @NotNull Model model, Integer categoryId) {
        productService.listByPage(pageNumber, pageSize, helper, categoryId);
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("listCategories", listCategories);

        return "products/products";
    }

    @GetMapping("/new")
    public String newProduct(@NotNull Model model) {
        List<Brand> listBrands = brandService.listAll();
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("numberOfExistingExtraImages", 0);

        return "products/product_form";
    }

    @PostMapping("/save")
    public String saveProduct(Product product, RedirectAttributes attributes,
                              @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
                              @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIds", required = false) String[] detailIds,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIds", required = false) String[] imageIds,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal @NotNull UserDetailsImpl loggedUser) throws IOException {
        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
            if (loggedUser.hasRole("Salesperson")) {
                productService.saveProductPrice(product);
                attributes.addFlashAttribute("message", "The product has been saved successfully");
                return defaultRedirectUrl;
            }
        }
        ProductSaveHelper.setMainImageName(mainImageMultipart, product);
        ProductSaveHelper.setExistingExtraImageNames(imageIds, imageNames, product);
        ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);
        ProductSaveHelper.setProductDetails(detailIds, detailNames, detailValues, product);
        Product savedProduct = productService.save(product);
        ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);
        ProductSaveHelper.deleteExtraImagesRemovedOnForm(product);

        attributes.addFlashAttribute("message", "The product has been saved successfully");
        return defaultRedirectUrl;
    }


    @GetMapping("/{id}/enabled/{status}")
    public String updateProductEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                                             @NotNull RedirectAttributes attributes, @NotNull HttpServletRequest request) {
        productService.updateEnabledStatus(id, enabled);
        attributes.addFlashAttribute("message", String.format("The product ID %s has been %s", id, enabled ? "enabled" : "disabled"));
        return String.format("redirect:%s", request.getHeader("Referer"));
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, @NotNull RedirectAttributes attributes, @NotNull HttpServletRequest request) {
        try {
            productService.delete(id);

            if (S3_SERVICE_ACTIVE) {
                String productExtraImagesS3Dir = "product-images/" + id + "/extras";
                String productImagesS3Dir = "product-images/" + id;
                AmazonS3Util.removeFolder(productExtraImagesS3Dir);
                AmazonS3Util.removeFolder(productImagesS3Dir);
            } else {
                String productExtraImagesDir = "../product-images/" + id + "/extras";
                String productImagesDir = "../product-images/" + id;
                FileUploadUtil.removeDir(productExtraImagesDir);
                FileUploadUtil.removeDir(productImagesDir);
            }
            attributes.addFlashAttribute("message", String.format("The product ID %s has been deleted successfully", id));
        } catch (ProductNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
        }

        return String.format("redirect:%s", request.getHeader("Referer"));
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes,
                              @AuthenticationPrincipal @NotNull UserDetailsImpl loggedUser) {
        try {
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();
            boolean isReadOnlyForSalesperson = false;

            if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
                if (loggedUser.hasRole("Salesperson")) {
                    isReadOnlyForSalesperson = true;
                }
            }

            model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", String.format("Edit Product (ID: %s)", id));
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "products/product_form";
        } catch (ProductNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectUrl;
        }
    }

    @GetMapping("/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, @NotNull Model model, RedirectAttributes attributes) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
            return "products/product_detail_modal";
        } catch (ProductNotFoundException e) {
            attributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectUrl;
        }
    }
}
