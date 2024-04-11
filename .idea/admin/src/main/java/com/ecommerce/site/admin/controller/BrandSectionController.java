package com.ecommerce.site.admin.controller;


import com.ecommerce.site.admin.exception.SectionNotFoundException;
import com.ecommerce.site.admin.model.entity.Brand;
import com.ecommerce.site.admin.model.entity.BrandSection;
import com.ecommerce.site.admin.model.entity.Section;
import com.ecommerce.site.admin.model.enums.SectionType;
import com.ecommerce.site.admin.service.BrandService;
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

import java.util.List;


@Controller
public class BrandSectionController {

	@Autowired
	private SectionService sectionService;
	
	@Autowired
	private BrandService brandService;
	
	@GetMapping("/sections/new/brand")
	public String showForm(@NotNull Model model) {
		Section section = new Section(true, SectionType.BRAND);
		List<Brand> listBrands = brandService.listAll();
		
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("section", section);
		model.addAttribute("pageTitle", "Add Brand Section");
		
		return "sections/brand_section_form";
	}		
	
	@PostMapping("/sections/save/brand")
	public String saveSection(Section section, HttpServletRequest request, @NotNull RedirectAttributes attributes) {
		addBrandsToSection(section, request);
		sectionService.saveSection(section);
		attributes.addFlashAttribute("message", "The section of type Brand has been saved successfully");
		return "redirect:/sections";
	}	
	
	private void addBrandsToSection(Section section, @NotNull HttpServletRequest request) {
		String[] IDs = request.getParameterValues("chosenBrands");
		
		if (IDs != null && IDs.length > 0) {
			for (int i = 0; i < IDs.length; i++) {
				String[] arrayIds = IDs[i].split("-");
				BrandSection brandSection = new BrandSection();

				int brandSectionId = Integer.parseInt(arrayIds[1]);
				if (brandSectionId > 0) {
					brandSection.setId(brandSectionId);
				}
					
				brandSection.setBrandOrder(i);
				Integer brandId = Integer.valueOf(arrayIds[0]);
				brandSection.setBrand(new Brand(brandId));
				section.addBrandSection(brandSection);
			}
		}		
	}	
	
	@GetMapping("/sections/edit/Brand/{id}")
	public String editSection(@PathVariable("id") Integer id, RedirectAttributes attributes, @NotNull Model model) {
		try {
			Section section = sectionService.getSection(id);
			List<Brand> listBrands = brandService.listAll();
			
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("section", section);
			model.addAttribute("pageTitle", "Edit Brand Section (ID: " + id + ")");
			
			return "sections/brand_section_form";
		} catch (SectionNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/sections";		
		}
	}	
}
