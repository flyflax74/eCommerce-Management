package com.ecommerce.site.admin.controller;

import java.util.List;

import com.ecommerce.site.admin.model.enums.MenuMoveDirection;
import com.ecommerce.site.admin.service.MenuService;
import com.ecommerce.site.admin.exception.MenuUnmoveableException;
import com.ecommerce.site.admin.exception.MenuItemNotFoundException;
import com.ecommerce.site.admin.model.entity.Article;
import com.ecommerce.site.admin.model.entity.Menu;
import com.ecommerce.site.admin.service.ArticleService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
public class MenuController {

	private final String defaultRedirectUrl = "redirect:/menus";
	
	@Autowired
	private MenuService menuService;

	@Autowired
	private ArticleService articleService;
	
	@GetMapping("/menus")
	public String listAll(@NotNull Model model) {
		List<Menu> listMenuItems = menuService.listAll();
		model.addAttribute("listMenuItems", listMenuItems);
		return "menus/menu_items";
	}	

	@GetMapping("menus/new")
	public String newMenu(@NotNull Model model) {
		List<Article> listArticles = articleService.listArticlesForMenu();

		model.addAttribute("menu", new Menu());
		model.addAttribute("listArticles", listArticles);
		model.addAttribute("pageTitle", "Create New Menu Item");

		return "menus/menu_form";
	}
	
	@PostMapping("/menus/save")
	public String saveMenu(Menu menu, @NotNull RedirectAttributes attributes) {
		menuService.save(menu);
		attributes.addFlashAttribute("message", "The menu item has been saved successfully");
		return defaultRedirectUrl;
	}	
	
	@GetMapping("/menus/edit/{id}")
	public String editMenu(@PathVariable("id") Integer id, @NotNull Model model, RedirectAttributes attributes) {
		try {
			Menu menu = menuService.get(id);
			List<Article> listArticles = articleService.listArticlesForMenu();
			
			model.addAttribute("menu", menu);
			model.addAttribute("listArticles", listArticles);
			model.addAttribute("pageTitle", String.format("Edit Menu Item (ID %s)", id));
			
			return "menus/menu_form";
		} catch (MenuItemNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectUrl;
		}
	}	
	
	@GetMapping("/menus/{id}/enabled/{enabledStatus}")
	public String updateMenuEnabledStatus(@PathVariable("id") Integer id, @PathVariable("enabledStatus") String enabledStatus,
										  @NotNull RedirectAttributes attributes) {
		try {
			boolean enabled = Boolean.parseBoolean(enabledStatus);					
			menuService.updateEnabledStatus(id, enabled);
			attributes.addFlashAttribute("message", String.format("The menu item ID %s has been %s", id, enabled ? "enabled." : "disabled."));
		} catch (MenuItemNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectUrl;
	}
	
	@GetMapping("/menus/delete/{id}")
	public String deleteMenu(@PathVariable("id") Integer id, @NotNull RedirectAttributes attributes) {
		try {
			menuService.delete(id);
			attributes.addFlashAttribute("message", String.format("The menu item ID %s has been deleted", id));
		} catch (MenuItemNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectUrl;
	}	
	
	@GetMapping("/menus/{direction}/{id}")
	public String moveMenu(@PathVariable("direction") @NotNull String direction,
						   @PathVariable("id") Integer id, @NotNull RedirectAttributes attributes) {
		try {
			MenuMoveDirection moveDirection = MenuMoveDirection.valueOf(direction.toUpperCase());
			menuService.moveMenu(id, moveDirection);
			attributes.addFlashAttribute("message", String.format("The menu ID %s has been moved up by one position", id));
		} catch (MenuUnmoveableException | MenuItemNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectUrl;
	}	
}
