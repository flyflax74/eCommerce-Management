package com.ecommerce.site.admin.rest;

import com.ecommerce.site.admin.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class OrderRestController {

	@Autowired
	private OrderService service;

	@PostMapping("/orders_shipper/update/{id}/{status}")
	public Response updateOrderStatus(@PathVariable("id") Integer orderId, @PathVariable("status") String status) {
		service.updateStatus(orderId, status);
		return new Response(orderId, status);
	}
}

@Getter
@Setter
@AllArgsConstructor
class Response {

	private Integer orderId;

	private String status;
}
