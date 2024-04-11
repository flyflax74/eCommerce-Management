package com.ecommerce.site.admin.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import com.ecommerce.site.admin.model.enums.SectionType;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "sections")
@Getter
@Setter
@NoArgsConstructor
public class Section implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@Column(nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sections_gen")
	@TableGenerator(name = "sections_gen",
			table = "sequencers",
			pkColumnName = "seq_name",
			valueColumnName = "seq_count",
			pkColumnValue = "sections_seq_next_val",
			allocationSize = 1)
	private Integer id;
	
	@Column(length = 256, nullable = false, unique = true)
	private String heading;
	
	@Column(length = 2048, nullable = false)
	private String description;
	
	private boolean enabled;
	
	@Column(name = "section_order")
	private int sectionOrder;

	@Enumerated(EnumType.STRING)
	private SectionType type;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "section_id")
	@OrderBy("productOrder ASC")
	private Set<ProductSection> productSections = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "section_id")
	@OrderBy("categoryOrder ASC")
	private Set<CategorySection> categorySections = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "section_id")
	@OrderBy("brandOrder ASC")
	private Set<BrandSection> brandSections = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "section_id")
	@OrderBy("articleOrder ASC")
	private List<ArticleSection> articleSections = new ArrayList<>();

	public Section(Integer id) {
		this.id = id;
	}

	public Section(boolean enabled, SectionType type) {
		this.enabled = enabled;
		this.type = type;
	}

	public Section(Integer id, String heading, SectionType type, int order, boolean enabled) {
		this.id = id;
		this.heading = heading;
		this.type = type;
		this.sectionOrder = order;
		this.enabled = enabled;
	}

	public void addProductSection(ProductSection productSection) {
		this.productSections.add(productSection);
	}

	public void addCategorySection(CategorySection categorySection) {
		this.categorySections.add(categorySection);
	}

	public void addBrandSection(BrandSection brandSection) {
		this.brandSections.add(brandSection);
	}

	public void addArticleSection(ArticleSection articleSection) {
		this.articleSections.add(articleSection);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Section section = (Section) o;

		return Objects.equals(id, section.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Section{" +
				"id=" + id +
				", heading='" + heading + '\'' +
				", enabled=" + enabled +
				", sectionOrder=" + sectionOrder +
				", type=" + type +
				'}';
	}
}
