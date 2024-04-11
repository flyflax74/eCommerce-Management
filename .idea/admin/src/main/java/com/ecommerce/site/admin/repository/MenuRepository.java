package com.ecommerce.site.admin.repository;

import com.ecommerce.site.admin.model.entity.Menu;
import com.ecommerce.site.admin.model.enums.MenuType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

	List<Menu> findAllByOrderByTypeAscPositionAsc();

	List<Menu> findByTypeOrderByPositionAsc(MenuType type);
	
	Long countByType(MenuType type);
	
	@Query("UPDATE Menu m SET m.enabled = ?2 WHERE m.id = ?1")
	@Modifying
	void updateEnabledStatus(Integer id, boolean enabled);
}
