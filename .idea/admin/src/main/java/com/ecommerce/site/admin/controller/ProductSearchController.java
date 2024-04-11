package com.ecommerce.site.admin.controller;

import com.ecommerce.site.admin.annotation.PagingAndSortingParam;
import com.ecommerce.site.admin.helper.PagingAndSortingHelper;
import com.ecommerce.site.admin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/orders")
public class ProductSearchController {

	@Autowired
	private ProductService service;
	
	@GetMapping("/search_product")
	public String showSearchProductPage() {
		return "orders/search_product";
	}
	
	@PostMapping("/search_product")
	public String searchProducts(String keyword) {
		return "redirect:/orders/search_product/offset/10/page/1?sortField=name&sortDir=asc&keyword=" + keyword;
	}
	
	@GetMapping("/search_product/offset/{pageSize}/page/{pageNumber}")
	public String searchProductsByPage(@PagingAndSortingParam(listName = "listProducts", moduleUrl = "/orders/search_product") PagingAndSortingHelper helper,
									   @PathVariable("pageSize") int pageSize, @PathVariable("pageNumber") int pageNumber) {
		service.searchProducts(pageNumber, pageSize, helper);
		return "orders/search_product";
	}
}
