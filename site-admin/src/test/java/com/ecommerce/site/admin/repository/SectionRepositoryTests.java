package com.ecommerce.site.admin.repository;

import com.ecommerce.site.admin.model.entity.*;
import com.ecommerce.site.admin.model.enums.SectionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SectionRepositoryTests {
	
	@Autowired private SectionRepository repo;
	
	@Test
	public void testAddTextSection() {
		Section section = new Section();
		section.setHeading("Annoucement");
		section.setDescription("Shop Annoucement in Summer 2022: The great sales season is coming...");
		section.setType(SectionType.TEXT);
		section.setSectionOrder(1);
		
		Section savedSection = repo.save(section);
		
		assertThat(savedSection).isNotNull();
		assertThat(savedSection.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListSectionsNotSorted() {
		List<Section> sections = repo.findAll();
		assertThat(sections).isNotEmpty();
		
		sections.forEach(System.out::println);
	}
	
	@Test
	public void testListSectionsSorted() {
		List<Section> sections = repo.findAllSectionsSortedByOrder();
		
		assertThat(sections).isNotEmpty();		
		sections.forEach(System.out::println);		
	}

	@Test
	public void testAddAllCategoriesSection() {
		Section section = new Section();
		section.setHeading("Shopping by Categories");
		section.setDescription("Check out all categories...");
		section.setType(SectionType.ALL_CATEGORIES);
		section.setSectionOrder(2);
		
		Section savedSection = repo.save(section);
		
		assertThat(savedSection).isNotNull();
		assertThat(savedSection.getId()).isGreaterThan(0);		
	}	
	
	@Test
	public void testAddCategorySection() {
		Section section = new Section();
		section.setHeading("Featured Categories");
		section.setDescription("Check out these featured categories...");
		section.setType(SectionType.CATEGORY);
		section.setSectionOrder(3);
		
		for (int i = 1; i <= 3; i++) {
			CategorySection categorySection = new CategorySection();
			
			Category category = new Category(i + 1);
			categorySection.setCategory(category);
			categorySection.setCategoryOrder(i);
			
			section.addCategorySection(categorySection);
		}	
		
		Section savedSection = repo.save(section);
		
		assertThat(savedSection).isNotNull();
		assertThat(savedSection.getId()).isGreaterThan(0);		
	}
	
	@Test
	public void testAddProductSection() {
		Section section = new Section();
		section.setHeading("Featured Products");
		section.setDescription("Check out these best-selling items...");
		section.setType(SectionType.PRODUCT);
		section.setSectionOrder(4);		
		
		for (int i = 1; i <= 3; i++) {
			ProductSection productSection = new ProductSection();
			Product product = new Product(i);
			
			productSection.setProduct(product);
			productSection.setProductOrder(i);
			
			section.addProductSection(productSection);
		}
		
		Section savedSection = repo.save(section);
		
		assertThat(savedSection).isNotNull();
		assertThat(savedSection.getId()).isGreaterThan(0);		
	}

	@Test
	public void testAddBrandSection() {
		Section section = new Section();
		section.setHeading("Featured Brands");
		section.setDescription("Recommended brands for shopping...");
		section.setType(SectionType.BRAND);
		section.setSectionOrder(5);	
		
		for (int i = 1; i <= 3; i++) {
			BrandSection brandSection = new BrandSection();
			Brand brand = new Brand();
			brand.setId(i);
			
			brandSection.setBrandOrder(i);
			brandSection.setBrand(brand);
			
			section.addBrandSection(brandSection);
		}
		
		Section savedSection = repo.save(section);
		
		assertThat(savedSection).isNotNull();
		assertThat(savedSection.getId()).isGreaterThan(0);		
	}	

	@Test
	public void testAddArticleSection() {
		Section section = new Section();
		section.setHeading("Shopping Tips");
		section.setDescription("Read these articles before shopping...");
		section.setType(SectionType.ARTICLE);
		section.setSectionOrder(6);	
		
		for (int i = 1; i <= 3; i++) {
			ArticleSection articleSection = new ArticleSection();
			
			articleSection.setArticle(new Article(4 + i));
			articleSection.setArticleOrder(i);
			
			section.addArticleSection(articleSection);
		}
		
		Section savedSection = repo.save(section);
		
		assertThat(savedSection).isNotNull();
		assertThat(savedSection.getId()).isGreaterThan(0);		
	}
	
	@Test
	public void testDeleteSection() {
		Integer sectionId = 3;
		repo.deleteById(sectionId);
		Optional<Section> findById = repo.findById(sectionId);
		
		assertThat(findById).isNotPresent();
	}
	
	@Test
	public void testDisableSection() {
		Integer sectionId = 7;
		repo.updateEnabledStatus(sectionId, false);
		Section section = repo.findById(sectionId).get();
		
		assertThat(section.isEnabled()).isFalse();
	}
	
	@Test
	public void testEnableSection() {
		Integer sectionId = 7;
		repo.updateEnabledStatus(sectionId, true);
		Section section = repo.findById(sectionId).get();
		
		assertThat(section.isEnabled()).isTrue();
	}
	
	@Test
	public void testGetSimpleSectionById() {
		Integer sectionId = 6;
		Section section = repo.getSimpleSectionById(sectionId);
		
		assertThat(section).isNotNull();
	}
	
	@Test
	public void testGetSectionIDsSortedByOrder() {
		List<Section> sections = repo.getOnlySectionIDsSortedByOrder();
		assertThat(sections).isNotEmpty();
		
		sections.forEach(System.out::println);
	}
	
	@Test
	public void testUpdateSectionPosition() {
		Integer sectionId = 5;
		Integer sectionOrder = 7;
		
		repo.updateSectionPosition(sectionOrder, sectionId);
		
		Section section = repo.findById(sectionId).get();
		assertThat(section.getSectionOrder()).isEqualTo(sectionOrder);
	}
}
