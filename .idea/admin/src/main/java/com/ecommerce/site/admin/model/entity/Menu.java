package com.ecommerce.site.admin.model.entity;

import com.ecommerce.site.admin.model.enums.MenuType;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(name = "menus")
@Getter
@Setter
public class Menu implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@Column(nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "menus_gen")
	@TableGenerator(name = "menus_gen",
			table = "sequencers",
			pkColumnName = "seq_name",
			valueColumnName = "seq_count",
			pkColumnValue = "menus_seq_next_val",
			allocationSize = 1)
	private Integer id;
	
	@Enumerated(EnumType.ORDINAL)
	private MenuType type;

	@Column(nullable = false, length = 128, unique = true)
	private String title;
	
	@Column(nullable = false, length = 256, unique = true)
	private String alias;
	
	private int position;
	
	private boolean enabled;
	
	@ManyToOne
	@JoinColumn(name = "article_id")
	private Article article;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Menu menu = (Menu) o;

		return Objects.equals(id, menu.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Menu{" +
				"id=" + id +
				", type=" + type +
				", title='" + title + '\'' +
				", position=" + position +
				'}';
	}

}
