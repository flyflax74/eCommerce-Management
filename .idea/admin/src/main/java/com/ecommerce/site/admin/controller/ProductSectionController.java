package com.ecommerce.site.admin.controller;


import com.ecommerce.site.admin.exception.SectionNotFoundException;
import com.ecommerce.site.admin.model.entity.Product;
import com.ecommerce.site.admin.model.entity.ProductSection;
import com.ecommerce.site.admin.model.entity.Section;
import com.ecommerce.site.admin.model.enums.SectionType;
import com.ecommerce.site.admin.service.SectionService;
import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductSectionController {

	@Autowired
	private SectionService service;
	
	@GetMapping("/sections/new/product")
	public String showForm(@NotNull Model model) {
		Section section = new Section(true, SectionType.PRODUCT);
		
		model.addAttribute("section", section);
		model.addAttribute("pageTitle", "Add Product Section");
		
		return "sections/product_section_form";
	}
	
	
	@PostMapping("/sections/save/product")
	public String saveSection(Section section, HttpServletRequest request, @NotNull RedirectAttributes attributes) {
		addProductsToSection(section, request);
		service.saveSection(section);
		attributes.addFlashAttribute("message", "The section of type Product has been saved successfully");
		return "redirect:/sections";
	}
	
	private void addProductsToSection(Section section, @NotNull HttpServletRequest request) {
		String[] productIds = request.getParameterValues("productId");
		String[] productSectionIds = request.getParameterValues("productSectionId");
		
		if (productIds != null && productIds.length > 0) {
			for (int i = 0; i < productIds.length; i++) {
				ProductSection productSection = new ProductSection();
				
				if (productSectionIds != null && productSectionIds.length > 0) {
					if (i < productSectionIds.length) {
						Integer productSectionId = Integer.valueOf(productSectionIds[i]);
						productSection.setId(productSectionId);
					}
				}
					
				productSection.setProductOrder(i);
				Integer productId = Integer.valueOf(productIds[i]);
				productSection.setProduct(new Product(productId));
				
				section.addProductSection(productSection);
			}
		}
	}
	
	@GetMapping("/sections/edit/Product/{id}")
	public String editSection(@PathVariable("id") Integer id, RedirectAttributes attributes, @NotNull Model model) {
		try {
			Section section = service.getSection(id);
			
			model.addAttribute("section", section);
			model.addAttribute("pageTitle", String.format("Edit Product Section (ID %s)", id));
			
			return "sections/product_section_form";
		} catch (SectionNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/sections";		
		}
	}
}

