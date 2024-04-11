package com.ecommerce.site.admin.model.entity;

import com.ecommerce.site.admin.model.entity.Brand;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "sections_brands")
@Getter
@Setter
public class BrandSection implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@Column(nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sections_brands_gen")
	@TableGenerator(name = "sections_brands_gen",
			table = "sequencers",
			pkColumnName = "seq_name",
			valueColumnName = "seq_count",
			pkColumnValue = "sections_brands_seq_next_val",
			allocationSize = 1)
	private Integer id;

	@Column(name = "brand_order")
	private int brandOrder;

	@ManyToOne
	@JoinColumn(name = "brand_id")
	private Brand brand;
}
