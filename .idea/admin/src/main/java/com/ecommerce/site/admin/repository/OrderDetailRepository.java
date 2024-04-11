package com.ecommerce.site.admin.repository;

import java.util.Date;
import java.util.List;

import com.ecommerce.site.admin.model.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
	
	@Query("SELECT NEW OrderDetail(od.product.category, od.quantity,"
			+ " od.productCost, od.shippingCost, od.subtotal)"
			+ " FROM OrderDetail od WHERE od.order.orderTime BETWEEN ?1 AND ?2")
	List<OrderDetail> findWithCategoryAndTimeBetween(Date startTime, Date endTime);

	@Query("SELECT NEW OrderDetail(od.product.brand, od.quantity,"
			+ " od.productCost, od.shippingCost, od.subtotal)"
			+ " FROM OrderDetail od WHERE od.order.orderTime BETWEEN ?1 AND ?2")
	List<OrderDetail> findWithBrandAndTimeBetween(Date startTime, Date endTime);
	
	@Query("SELECT NEW OrderDetail(od.quantity, od.product.name,"
			+ " od.productCost, od.shippingCost, od.subtotal)"
			+ " FROM OrderDetail od WHERE od.order.orderTime BETWEEN ?1 AND ?2")
	List<OrderDetail> findWithProductAndTimeBetween(Date startTime, Date endTime);
}
