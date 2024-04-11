package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.exception.SectionNotFoundException;
import com.ecommerce.site.admin.model.entity.Article;
import com.ecommerce.site.admin.model.entity.ArticleSection;
import com.ecommerce.site.admin.model.entity.Section;
import com.ecommerce.site.admin.model.enums.SectionType;
import com.ecommerce.site.admin.service.ArticleService;
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
public class ArticleSectionController {

	@Autowired
	private SectionService sectionService;
	
	@Autowired
	private ArticleService articleService;
	
	@GetMapping("/sections/new/article")
	public String showForm(@NotNull Model model) {
		Section section = new Section(true, SectionType.ARTICLE);
		List<Article> listArticles = articleService.listAll();
		
		model.addAttribute("listArticles", listArticles);
		model.addAttribute("section", section);
		model.addAttribute("pageTitle", "Add Article Section");
		
		return "sections/article_section_form";
	}			
	
	@PostMapping("/sections/save/article")
	public String saveSection(Section section, HttpServletRequest request, @NotNull RedirectAttributes attributes) {
		addArticlesToSection(section, request);
		sectionService.saveSection(section);
		attributes.addFlashAttribute("message", "The section of type Article has been saved successfully");
		return "redirect:/sections";
	}		
	
	private void addArticlesToSection(Section section, @NotNull HttpServletRequest request) {
		String[] IDs = request.getParameterValues("chosenArticles");
		
		if (IDs != null && IDs.length > 0) {
			for (int i = 0; i < IDs.length; i++) {
				String[] arrayIds = IDs[i].split("-");
				
				ArticleSection articleSection = new ArticleSection();
				
				int articleSectionId = Integer.parseInt(arrayIds[1]);
				if (articleSectionId > 0) {
					articleSection.setId(articleSectionId);
				}
					
				articleSection.setArticleOrder(i);
				Integer articleId = Integer.valueOf(arrayIds[0]);
				
				articleSection.setArticle(new Article(articleId));
				section.addArticleSection(articleSection);
			}
		}		
	}		
	
	@GetMapping("/sections/edit/Article/{id}")
	public String editSection(@PathVariable("id") Integer id, RedirectAttributes attributes, @NotNull Model model) {
		try {
			Section section = sectionService.getSection(id);
			List<Article> listArticles = articleService.listAll();
			
			model.addAttribute("listArticles", listArticles);
			model.addAttribute("section", section);
			model.addAttribute("pageTitle", "Edit Article Section (ID: " + id + ")");
			
			return "sections/article_section_form";
		} catch (SectionNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/sections";		
		}
	}		
}
