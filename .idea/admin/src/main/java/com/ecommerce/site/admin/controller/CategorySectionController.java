package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.exception.SectionNotFoundException;
import com.ecommerce.site.admin.model.entity.Category;
import com.ecommerce.site.admin.model.entity.CategorySection;
import com.ecommerce.site.admin.model.entity.Section;
import com.ecommerce.site.admin.model.enums.SectionType;
import com.ecommerce.site.admin.service.CategoryService;
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
public class CategorySectionController {

	@Autowired
	private SectionService sectionService;

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/sections/new/category")
	public String showForm(@NotNull Model model) {
		Section section = new Section(true, SectionType.CATEGORY);
		List<Category> listCategories = categoryService.listAll();
		
		model.addAttribute("listCategories", listCategories);		
		model.addAttribute("section", section);
		model.addAttribute("pageTitle", "Add Category Section");
		
		return "sections/category_section_form";
	}	
	
	@PostMapping("/sections/save/category")
	public String saveSection(Section section, HttpServletRequest request, @NotNull RedirectAttributes attributes) {
		addCategoriesToSection(section, request);
		sectionService.saveSection(section);
		attributes.addFlashAttribute("message", "The section of type Category has been saved successfully");
		return "redirect:/sections";
	}	
	
	private void addCategoriesToSection(Section section, @NotNull HttpServletRequest request) {
		String[] IDs = request.getParameterValues("chosenCategories");
		
		if (IDs != null && IDs.length > 0) {
			for (int i = 0; i < IDs.length; i++) {
				String[] arrayIds = IDs[i].split("-");
				
				CategorySection categorySection = new CategorySection();
				
				int categorySectionId = Integer.parseInt(arrayIds[1]);
				if (categorySectionId > 0) {
						categorySection.setId(categorySectionId);
				}
					
				categorySection.setCategoryOrder(i);
				Integer categoryId = Integer.valueOf(arrayIds[0]);
				
				categorySection.setCategory(new Category(categoryId));
				section.addCategorySection(categorySection);
			}
		}		
	}
	
	@GetMapping("/sections/edit/Category/{id}")
	public String editSection(@PathVariable("id") Integer id, RedirectAttributes rattributes, @NotNull Model model) {
		try {
			Section section = sectionService.getSection(id);
			List<Category> listCategories = categoryService.listAll();
			
			model.addAttribute("listCategories", listCategories);			
			model.addAttribute("section", section);
			model.addAttribute("pageTitle", "Edit Category Section (ID: " + id + ")");
			
			return "sections/category_section_form";
			
		} catch (SectionNotFoundException ex) {
			rattributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/sections";		
		}
		
	}	
}
