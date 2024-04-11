package com.ecommerce.site.admin.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.ecommerce.site.admin.model.entity.Article;
import com.ecommerce.site.admin.model.enums.ArticleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ArticleRepositoryTests {

	@Autowired private ArticleRepository repository;
	
	@Test
	public void testListMenuBoundArticles() {
		List<Article> listArticles = repository.findByTypeOrderByTitle(ArticleType.MENU_BOUND);
		assertThat(listArticles).isNotEmpty();
		
		listArticles.forEach(System.out::println);
	}
}
