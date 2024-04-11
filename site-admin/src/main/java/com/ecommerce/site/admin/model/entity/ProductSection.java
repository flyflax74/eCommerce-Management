package com.ecommerce.site.admin.model.entity;

import com.ecommerce.site.admin.model.entity.Product;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "sections_products")
@Getter
@Setter
public class ProductSection implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@Column(nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sections_products_gen")
	@TableGenerator(name = "sections_products_gen",
			table = "sequencers",
			pkColumnName = "seq_name",
			valueColumnName = "seq_count",
			pkColumnValue = "sections_products_seq_next_val",
			allocationSize = 1)
	private Integer id;

	@Column(name = "product_order")
	private int productOrder;
	
	@ManyToOne
	@JoinColumn(name = "product_id")	
	private Product product;
}
