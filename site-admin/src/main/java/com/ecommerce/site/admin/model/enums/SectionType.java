package com.ecommerce.site.admin.model.enums;



public enum SectionType {
	PRODUCT {
		public String toString() { return "Product"; }
	}, 
	
	CATEGORY {
		public String toString() { return "Category"; }
	},
	
	BRAND {
		public String toString() { return "Brand"; }
	}, 
	
	ARTICLE {
		public String toString() { return "Article"; }
	}, 
	
	TEXT {
		public String toString() { return "Text"; }
	},
	
	ALL_CATEGORIES {
		public String toString() { return "All_Categories"; }
	}	
}
