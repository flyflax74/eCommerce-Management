package com.ecommerce.site.admin.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.ecommerce.site.admin.model.entity.Article;
import com.ecommerce.site.admin.model.entity.Menu;
import com.ecommerce.site.admin.model.enums.MenuType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class MenuRepositoryTests {

	@Autowired private MenuRepository repository;
	
	@Test
	public void testCreateHeaderMenu() {
		Menu menu = new Menu();
		menu.setType(MenuType.HEADER);
		menu.setTitle("About Shopme");
		menu.setAlias("about");
		menu.setEnabled(true);
		menu.setPosition(1);
		
		menu.setArticle(new Article(1));
		
		Menu savedMenu = repository.save(menu);
		
		assertTrue(savedMenu.getId() > 0);
	}
	
	@Test
	public void testCreateFooterMenu() {
		Menu menu = new Menu();
		menu.setType(MenuType.FOOTER);
		menu.setTitle("Shipping");
		menu.setAlias("shipping");
		menu.setEnabled(false);
		menu.setPosition(2);
		
		menu.setArticle(new Article(4));
		
		Menu savedMenu = repository.save(menu);
		
		assertTrue(savedMenu.getId() > 0);
	}	
	
	@Test
	public void testListMenuByTypeThenByPosition() {
		List<Menu> listMenuItems = repository.findAllByOrderByTypeAscPositionAsc();
		assertThat(listMenuItems.size()).isGreaterThan(0);
		
		listMenuItems.forEach(System.out::println);
	}
	
	@Test
	public void testCountFooterMenus() {
		Long numberOfFooterMenus = repository.countByType(MenuType.FOOTER);
		assertEquals(1, numberOfFooterMenus);
	}	
	
	@Test
	public void testDisableMenuItem() {
		Integer menuId = 1;
		repository.updateEnabledStatus(menuId, false);
		Menu updatedMenu = repository.findById(menuId).get();
		
		assertFalse(updatedMenu.isEnabled());
	}
	
	@Test
	public void testEnableMenuItem() {
		Integer menuId = 1;
		repository.updateEnabledStatus(menuId, true);
		Menu updatedMenu = repository.findById(menuId).get();
		
		assertTrue(updatedMenu.isEnabled());
	}	
	
	@Test
	public void testListHeaderMenuItems() {
		List<Menu> listHeaderMenuItems = repository.findByTypeOrderByPositionAsc(MenuType.HEADER);
		assertThat(listHeaderMenuItems).isNotEmpty();
		
		listHeaderMenuItems.forEach(System.out::println);
	}
	
	@Test
	public void testListFooterMenuItems() {
		List<Menu> listFooterMenuItems = repository.findByTypeOrderByPositionAsc(MenuType.FOOTER);
		assertThat(listFooterMenuItems).isNotEmpty();
		
		listFooterMenuItems.forEach(System.out::println);
	}	
}
