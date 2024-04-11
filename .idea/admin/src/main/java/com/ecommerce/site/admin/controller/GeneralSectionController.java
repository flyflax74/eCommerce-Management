package com.ecommerce.site.admin.controller;


import com.ecommerce.site.admin.exception.SectionNotFoundException;
import com.ecommerce.site.admin.exception.SectionUnmoveableException;
import com.ecommerce.site.admin.model.entity.Section;
import com.ecommerce.site.admin.service.SectionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;



@Controller
public class GeneralSectionController {

	@Autowired
	private SectionService service;
	
	@GetMapping("/sections")
	public String listAllSections(@NotNull Model model) {
		List<Section> listSections = service.listSections();
		model.addAttribute("listSections", listSections);
		return "sections/sections";
	}

	@GetMapping("/sections/delete/{id}")
	public String deleteSection(@PathVariable("id") Integer id, @NotNull RedirectAttributes attributes) {
		try {
			service.deleteSection(id);
			attributes.addFlashAttribute("message", String.format("The section ID %s has been deleted", id));
		} catch (SectionNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/sections";		
	}
	
	@GetMapping("/sections/{id}/enabled/{enabledStatus}")
	public String updateSectionEnabledStatus(@PathVariable("id") Integer id,
											 @PathVariable("enabledStatus") String enabledStatus,
											 @NotNull RedirectAttributes attributes) {
		try {
			boolean enabled = Boolean.parseBoolean(enabledStatus);
			service.updateSectionEnabledStatus(id, enabled);
			attributes.addFlashAttribute("message", String.format("The section ID %s has been %s", id, enabled ? "enabled" : "disabled"));
		} catch (SectionNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/sections";
	}
	
	@GetMapping("/sections/moveup/{id}")
	public String moveSectionUp(@PathVariable("id") Integer id, @NotNull RedirectAttributes attributes) {
		try {
			service.moveSectionUp(id);
			attributes.addFlashAttribute("message", String.format("The section ID %s has been moved up by one position", id));
		} catch (SectionUnmoveableException | SectionNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/sections";		
	}
	
	@GetMapping("/sections/movedown/{id}")
	public String moveSectionDown(@PathVariable("id") Integer id, @NotNull RedirectAttributes attributes) {
		try {
			service.moveSectionDown(id);
			attributes.addFlashAttribute("message", String.format("The section ID %s has been moved down by one position", id));
		} catch (SectionUnmoveableException | SectionNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/sections";		
	}	
}
