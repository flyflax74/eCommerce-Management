package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.exception.SectionNotFoundException;
import com.ecommerce.site.admin.model.entity.Section;
import com.ecommerce.site.admin.model.enums.SectionType;
import com.ecommerce.site.admin.service.SectionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AllCategoriesSectionController {

	@Autowired
	private SectionService service;
	
	@GetMapping("/sections/new/all_categories")
	public String showForm(@NotNull Model model) {
		Section section = new Section(true, SectionType.ALL_CATEGORIES);
		
		model.addAttribute("section", section);
		model.addAttribute("pageTitle", "Add All Categories Section");
		
		return "sections/all_categories_section_form";
	}	
	
	@PostMapping("/sections/save/all_categories")
	public String saveSection(Section section, @NotNull RedirectAttributes attributes) {
		service.saveSection(section);
		attributes.addFlashAttribute("message", "The section of type All Categories has been saved successfully");
		return "redirect:/sections";
	}		
	
	@GetMapping("/sections/edit/All_Categories/{id}")
	public String editSection(@PathVariable("id") Integer id, RedirectAttributes attributes, @NotNull Model model) {
		try {
			Section section = service.getSection(id);
			
			model.addAttribute("section", section);
			model.addAttribute("pageTitle", String.format("Edit All Categories Section (ID %s)", id));
			
			return "sections/all_categories_section_form";
		} catch (SectionNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/sections";		
		}
	}	
}
