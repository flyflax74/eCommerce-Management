package com.ecommerce.site.admin.service;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


import com.ecommerce.site.admin.exception.MenuItemNotFoundException;
import com.ecommerce.site.admin.exception.MenuUnmoveableException;
import com.ecommerce.site.admin.model.entity.Menu;
import com.ecommerce.site.admin.model.enums.MenuMoveDirection;
import com.ecommerce.site.admin.repository.MenuRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class MenuService {

	@Autowired
	private MenuRepository repository;
	
	public List<Menu> listAll() {
		return repository.findAllByOrderByTypeAscPositionAsc();
	}	
	
	public void save(Menu menu) {
		setDefaultAlias(menu);
		
		if (menu.getId() == null) {
			setPositionForNewMenu(menu);
		} else {
			setPositionForEditedMenu(menu);
		}
		
		repository.save(menu);
	}
	
	private void setPositionForEditedMenu(@NotNull Menu menu) {
		Menu existMenu = repository.findById(menu.getId()).get();
		
		if (!existMenu.getType().equals(menu.getType())) {
			// if the menu type changed, then set its position at the last
			setPositionForNewMenu(menu);
		}
	}

	private void setPositionForNewMenu(@NotNull Menu newMenu) {
		// newly added menu always has position at the last
		long newPosition = repository.countByType(newMenu.getType()) + 1;
		newMenu.setPosition((int) newPosition);
	}
	
	private void setDefaultAlias(@NotNull Menu menu) {
		if (menu.getAlias() == null || menu.getAlias().isEmpty()) {
			menu.setAlias(menu.getTitle().replaceAll(" ", "-"));
		}		
	}
	
	public Menu get(Integer id) throws MenuItemNotFoundException {
		try {
			return repository.findById(id).orElseThrow();
		} catch (NoSuchElementException ex) {
			throw new MenuItemNotFoundException("Could not find any menu item with ID " + id);
		}
	}
	
	public void updateEnabledStatus(Integer id, boolean enabled) throws MenuItemNotFoundException {
		if (!repository.existsById(id)) {
			throw new MenuItemNotFoundException("Could not find any menu item with ID " + id);
		}
		repository.updateEnabledStatus(id, enabled);
	}
	
	public void delete(Integer id) throws MenuItemNotFoundException {
		if (!repository.existsById(id)) {
			throw new MenuItemNotFoundException("Could not find any menu item with ID " + id);
		}
		repository.deleteById(id);
	}
	
	public void moveMenu(Integer id, @NotNull MenuMoveDirection direction) throws MenuUnmoveableException, MenuItemNotFoundException {
		if (direction.equals(MenuMoveDirection.UP)) {
			moveMenuUp(id);
		} else {
			moveMenuDown(id);
		}
	}
	
	private void moveMenuUp(Integer id) throws MenuUnmoveableException, MenuItemNotFoundException {
		Optional<Menu> findById = repository.findById(id);
		if (findById.isEmpty()) {
			throw new MenuItemNotFoundException("Could not find any menu item with ID " + id);
		}
		
		Menu currentMenu = findById.get();
		List<Menu> allMenusOfSameType = repository.findByTypeOrderByPositionAsc(currentMenu.getType());
		int currentMenuIndex = allMenusOfSameType.indexOf(currentMenu);
		
		if (currentMenuIndex == 0) {
			throw new MenuUnmoveableException("The menu ID " + id + " is already in the first position");
		}
		
		// swap current menu item with the previous one, thus the given menu is moved up
		int previousMenuIndex = currentMenuIndex - 1;
		Menu previousMenu = allMenusOfSameType.get(previousMenuIndex);
		
		currentMenu.setPosition(previousMenuIndex + 1);
		allMenusOfSameType.set(previousMenuIndex, currentMenu);
		
		previousMenu.setPosition(currentMenuIndex + 1);
		allMenusOfSameType.set(currentMenuIndex, previousMenu);
		
		// update all menu items of the same type
		repository.saveAll(Arrays.asList(currentMenu, previousMenu));
	}
	
	private void moveMenuDown(Integer id) throws MenuUnmoveableException, MenuItemNotFoundException {
		Optional<Menu> findById = repository.findById(id);
		if (findById.isEmpty()) {
			throw new MenuItemNotFoundException("Could not find any menu item with ID " + id);
		}
		
		Menu currentMenu = findById.get();
		List<Menu> allMenusOfSameType = repository.findByTypeOrderByPositionAsc(currentMenu.getType());
		int currentMenuIndex = allMenusOfSameType.indexOf(currentMenu);
		
		if (currentMenuIndex == allMenusOfSameType.size() - 1) {
			throw new MenuUnmoveableException("The menu ID " + id + " is already in the last position");
		}
		
		// swap current menu item with the next one, thus the given menu is moved down
		int nextMenuIndex = currentMenuIndex + 1;
		Menu nextMenu = allMenusOfSameType.get(nextMenuIndex);
		
		currentMenu.setPosition(nextMenuIndex + 1);
		allMenusOfSameType.set(nextMenuIndex, currentMenu);
		
		nextMenu.setPosition(currentMenuIndex + 1);
		allMenusOfSameType.set(currentMenuIndex, nextMenu);

		// update all menu items of the same type
		repository.saveAll(Arrays.asList(currentMenu, nextMenu));
	}
}
