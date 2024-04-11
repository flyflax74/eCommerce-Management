package com.ecommerce.site.admin.model.entity;

import com.ecommerce.site.admin.model.entity.Category;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "sections_categories")
@Getter
@Setter
public class CategorySection implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@Column(nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sections_categories_gen")
	@TableGenerator(name = "sections_categories_gen",
			table = "sequencers",
			pkColumnName = "seq_name",
			valueColumnName = "seq_count",
			pkColumnValue = "sections_categories_seq_next_val",
			allocationSize = 1)
	private Integer id;
	
	@Column(name = "category_order")
	private int categoryOrder;
	
	@ManyToOne
	@JoinColumn(name = "category_id")	
	private Category category;
}
