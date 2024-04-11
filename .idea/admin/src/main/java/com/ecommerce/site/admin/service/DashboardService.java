package com.ecommerce.site.admin.service;


import java.util.List;


import com.ecommerce.site.admin.model.DashboardInfo;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DashboardService {

	@Autowired
	private EntityManager entityManager;

	public DashboardInfo loadSummary() {
		DashboardInfo summary = new DashboardInfo();
		Query query = entityManager.createQuery("SELECT "
					+ "(SELECT COUNT(DISTINCT u.id) AS totalUsers FROM User u),"
					+ "(SELECT COUNT(DISTINCT c.id) AS totalCategories FROM Category c), "
					+ "(SELECT COUNT(DISTINCT b.id) AS totalBrands FROM Brand b), "
					+ "(SELECT COUNT(DISTINCT p.id) AS totalProducts FROM Product p), "
					+ "(SELECT COUNT(DISTINCT q.id) AS totalQuestions FROM Question q), "
					+ "(SELECT COUNT(DISTINCT r.id) AS totalReviews FROM Review r), "
					+ "(SELECT COUNT(DISTINCT cu.id) AS totalCustomers FROM Customer cu), "
					+ "(SELECT COUNT(DISTINCT o.id) AS totalOrders FROM Order o), "
					+ "(SELECT COUNT(DISTINCT sr.id) AS totalShippingRates FROM ShippingRate sr), "
					+ "(SELECT COUNT(DISTINCT a.id) AS totalArticles FROM Article a), "
					+ "(SELECT COUNT(DISTINCT m.id) AS totalMenuItems FROM Menu m), "
					+ "(SELECT COUNT(DISTINCT se.id) AS totalSections FROM Section se), "
					+ "(SELECT COUNT(DISTINCT u.id) AS disabledUsers FROM User u WHERE u.enabled = FALSE), "
					+ "(SELECT COUNT(DISTINCT u.id) AS enabledUsers FROM User u WHERE u.enabled = TRUE), "
					+ "(SELECT COUNT(DISTINCT c.id) AS rootCategories FROM Category c WHERE c.parent IS NULL), "
					+ "(SELECT COUNT(DISTINCT c.id) AS enabledCategories FROM Category c WHERE c.enabled = TRUE), "
					+ "(SELECT COUNT(DISTINCT c.id) AS disabledCategories FROM Category c WHERE c.enabled = FALSE), "
					+ "(SELECT COUNT(DISTINCT p.id) AS enabledProducts FROM Product p WHERE p.enabled = TRUE), "
					+ "(SELECT COUNT(DISTINCT p.id) AS disabledProducts FROM Product p WHERE p.enabled = FALSE), "
					+ "(SELECT COUNT(DISTINCT p.id) AS inStockProducts FROM Product p WHERE p.inStock = TRUE), "
					+ "(SELECT COUNT(DISTINCT p.id) AS outOfStockProducts FROM Product p WHERE p.inStock = FALSE), "
					+ "(SELECT COUNT(DISTINCT q.id) AS approvedQuestions FROM Question q WHERE q.approved = TRUE), "
					+ "(SELECT COUNT(DISTINCT q.id) AS unapprovedQuestions FROM Question q WHERE q.approved = FALSE), "
					+ "(SELECT COUNT(DISTINCT q.id) AS answeredQuestions FROM Question q WHERE q.answer IS NOT NULL), "
					+ "(SELECT COUNT(DISTINCT q.id) AS unansweredQuestions FROM Question q WHERE q.answer IS NULL), "
					+ "(SELECT COUNT(DISTINCT cu.id) AS enabledCustomers FROM Customer cu WHERE cu.enabled = TRUE), "
					+ "(SELECT COUNT(DISTINCT cu.id) AS disabledCustomers FROM Customer cu WHERE cu.enabled = FALSE), "
					+ "(SELECT COUNT(DISTINCT sr.id) AS codShippingRates FROM ShippingRate sr WHERE sr.codSupported = TRUE), "
					+ "(SELECT COUNT(DISTINCT o.id) AS newOrders FROM Order o WHERE o.status = 'NEW'), "
					+ "(SELECT COUNT(DISTINCT o.id) AS deliveredOrders FROM Order o WHERE o.status = 'DELIVERED'), "
					+ "(SELECT COUNT(DISTINCT o.id) AS processingOrders FROM Order o WHERE o.status = 'PROCESSING'), "
					+ "(SELECT COUNT(DISTINCT o.id) AS shippingOrders FROM Order o WHERE o.status = 'SHIPPING'), "
					+ "(SELECT COUNT(DISTINCT o.id) AS cancelledOrders FROM Order o WHERE o.status = 'CANCELLED'), "
					+ "(SELECT COUNT(DISTINCT a.id) AS menuBoundArticles FROM Article a WHERE a.type = 0), "
					+ "(SELECT COUNT(DISTINCT a.id) AS freeArticles FROM Article a WHERE a.type = 1), "
					+ "(SELECT COUNT(DISTINCT a.id) AS publishedArticles FROM Article a WHERE a.published = TRUE), "
					+ "(SELECT COUNT(DISTINCT a.id) AS unpublishedArticles FROM Article a WHERE a.published = FALSE), "
					+ "(SELECT COUNT(DISTINCT m.id) AS headerMenuItems FROM Menu m WHERE m.type = 0), "
					+ "(SELECT COUNT(DISTINCT m.id) AS footerMenuItems FROM Menu m WHERE m.type = 1), "
					+ "(SELECT COUNT(DISTINCT m.id) AS enabledMenuItems FROM Menu m WHERE m.enabled = TRUE), "
					+ "(SELECT COUNT(DISTINCT m.id) AS disabledMenuItems FROM Menu m WHERE m.enabled = FALSE), "
					+ "(SELECT COUNT(DISTINCT s.id) AS enabledSections FROM Section s WHERE s.enabled = TRUE), "
					+ "(SELECT COUNT(DISTINCT s.id) AS disabledSections FROM Section s WHERE s.enabled = FALSE), "
					+ "(SELECT COUNT(DISTINCT r.product.id) AS reviewedProducts FROM Review r), "
					+ "(SELECT COUNT(DISTINCT c.id) AS totalCountries FROM Country c), "
					+ "(SELECT COUNT(DISTINCT s.id) AS totalStates FROM State s), "
					+ "st.value as siteName, "
					+ "(SELECT cr.name AS currencyName FROM Currency cr JOIN Setting st ON cr.id = st.value AND st.key = 'CURRENCY_ID'), "
					+ "(SELECT st.value AS currencySymbol FROM Setting st WHERE st.key = 'CURRENCY_SYMBOL'), "
					+ "(SELECT st.value AS decimalPointType FROM Setting st WHERE st.key = 'DECIMAL_POINT_TYPE'), "
					+ "(SELECT st.value AS decimalDigits FROM Setting st WHERE st.key = 'DECIMAL_DIGITS'), "
					+ "(SELECT st.value AS thousandsPointType FROM Setting st WHERE st.key = 'THOUSANDS_POINT_TYPE'), "
					+ "(SELECT st.value AS mailServer FROM Setting st WHERE st.key = 'MAIL_HOST') "
					+ "FROM Setting st WHERE st.key = 'SITE_NAME'");
		
		List<Object[]> entityCounts = query.getResultList();
		Object[] arrayCounts = entityCounts.get(0);
		
		int count = 0;
		summary.setTotalUsers((Long) arrayCounts[count++]);
		summary.setTotalCategories((Long) arrayCounts[count++]);
		summary.setTotalBrands((Long) arrayCounts[count++]);
		summary.setTotalProducts((Long) arrayCounts[count++]);
		summary.setTotalQuestions((Long) arrayCounts[count++]);
		summary.setTotalReviews((Long) arrayCounts[count++]);
		summary.setTotalCustomers((Long) arrayCounts[count++]);
		summary.setTotalOrders((Long) arrayCounts[count++]);
		summary.setTotalShippingRates((Long) arrayCounts[count++]);
		summary.setTotalArticles((Long) arrayCounts[count++]);
		summary.setTotalMenuItems((Long) arrayCounts[count++]);
		summary.setTotalSections((Long) arrayCounts[count++]);
		summary.setDisabledUsersCount((Long) arrayCounts[count++]);
		summary.setEnabledUsersCount((Long) arrayCounts[count++]);
		summary.setRootCategoriesCount((Long) arrayCounts[count++]);
		summary.setEnabledCategoriesCount((Long) arrayCounts[count++]);
		summary.setDisabledCategoriesCount((Long) arrayCounts[count++]);
		
		summary.setEnabledProductsCount((Long) arrayCounts[count++]);
		summary.setDisabledProductsCount((Long) arrayCounts[count++]);
		summary.setInStockProductsCount((Long) arrayCounts[count++]);
		summary.setOutOfStockProductsCount((Long) arrayCounts[count++]);

		summary.setApprovedQuestionsCount((Long) arrayCounts[count++]);
		summary.setUnapprovedQuestionsCount((Long) arrayCounts[count++]);
		summary.setAnsweredQuestionsCount((Long) arrayCounts[count++]);
		summary.setUnansweredQuestionsCount((Long) arrayCounts[count++]);

		summary.setEnabledCustomersCount((Long) arrayCounts[count++]);
		summary.setDisabledCustomersCount((Long) arrayCounts[count++]);
		
		summary.setCodShippingRateCount((Long) arrayCounts[count++]);
		summary.setNewOrdersCount((Long) arrayCounts[count++]);
		summary.setDeliveredOrdersCount((Long) arrayCounts[count++]);
		summary.setProcessingOrdersCount((Long) arrayCounts[count++]);
		summary.setShippingOrdersCount((Long) arrayCounts[count++]);
		summary.setCancelledOrdersCount((Long) arrayCounts[count++]);
		
		summary.setMenuBoundArticlesCount((Long) arrayCounts[count++]);
		summary.setFreeArticlesCount((Long) arrayCounts[count++]);
		summary.setPublishedArticlesCount((Long) arrayCounts[count++]);
		summary.setUnpublishedArticlesCount((Long) arrayCounts[count++]);
		
		summary.setHeaderMenuItemsCount((Long) arrayCounts[count++]);
		summary.setFooterMenuItemsCount((Long) arrayCounts[count++]);
		summary.setEnabledMenuItemsCount((Long) arrayCounts[count++]);
		summary.setDisabledMenuItemsCount((Long) arrayCounts[count++]);
		
		summary.setEnabledSectionsCount((Long) arrayCounts[count++]);
		summary.setDisabledSectionsCount((Long) arrayCounts[count++]);
		
		summary.setReviewedProductsCount((Long) arrayCounts[count++]);

		summary.setTotalCountries((Long) arrayCounts[count++]);
		summary.setTotalStates((Long) arrayCounts[count++]);
		
		summary.setSiteName((String) arrayCounts[count++]);
		summary.setCurrencyName((String) arrayCounts[count++]);
		summary.setCurrencySymbol((String) arrayCounts[count++]);
		summary.setDecimalPointType((String) arrayCounts[count++]);
		summary.setDecimalDigits((String) arrayCounts[count++]);
		summary.setThousandPointType((String) arrayCounts[count++]);
		summary.setMailServer((String) arrayCounts[count]);
		
		return summary;
	}
}