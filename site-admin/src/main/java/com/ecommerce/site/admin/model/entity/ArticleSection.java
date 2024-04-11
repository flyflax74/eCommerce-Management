package com.ecommerce.site.admin.model.entity;

import com.ecommerce.site.admin.model.entity.Article;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "sections_articles")
@Getter
@Setter
public class ArticleSection implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@Column(nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sections_articles_gen")
	@TableGenerator(name = "sections_articles_gen",
			table = "sequencers",
			pkColumnName = "seq_name",
			valueColumnName = "seq_count",
			pkColumnValue = "sections_articles_seq_next_val",
			allocationSize = 1)
	private Integer id;

	@Column(name = "article_order")
	private int articleOrder;

	@ManyToOne
	@JoinColumn(name = "article_id")
	private Article article;
}
