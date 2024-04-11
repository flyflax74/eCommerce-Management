package com.ecommerce.site.admin.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ecommerce.site.admin.repository.OrderRepository;
import com.ecommerce.site.admin.model.ReportItem;
import com.ecommerce.site.admin.model.enums.ReportType;
import com.ecommerce.site.admin.model.entity.Order;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MasterOrderReportService extends AbstractReportService {

	@Autowired
	private OrderRepository repository;

	
	protected List<ReportItem> getReportDataByDateRangeInternal(Date startTime, Date endTime, ReportType reportType) {
		List<Order> listOrders = repository.findByOrderTimeBetween(startTime, endTime);
		printRawData(listOrders);
		
		List<ReportItem> listReportItems = createReportData(startTime, endTime, reportType);
		
		System.out.println();
		
		calculateSalesForReportData(listOrders, listReportItems);
		printReportData(listReportItems);
		
		return listReportItems;
	}

	private void calculateSalesForReportData(@NotNull List<Order> listOrders, List<ReportItem> listReportItems) {
		for (Order order : listOrders) {
			String orderDateString = dateFormatter.format(order.getOrderTime());
			ReportItem reportItem = new ReportItem(orderDateString);
			int itemIndex = listReportItems.indexOf(reportItem);
			
			if (itemIndex >= 0) {
				reportItem = listReportItems.get(itemIndex);
				reportItem.addGrossSales(order.getTotal());
				reportItem.addNetSales(order.getSubtotal() - order.getProductCost());
				reportItem.increaseOrdersCount();
			}
		}
	}
	
	private void printReportData(@NotNull List<ReportItem> listReportItems) {
		listReportItems.forEach(item -> System.out.printf("%s, %10.2f, %10.2f, %d \n", item.getIdentifier(), item.getGrossSales(),
				item.getNetSales(), item.getOrdersCount()));
		
	}

	private @NotNull List<ReportItem> createReportData(Date startTime, Date endTime, @NotNull ReportType reportType) {
		List<ReportItem> listReportItems = new ArrayList<>();
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(startTime);
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(endTime);	
		
		Date currentDate = startDate.getTime();
		String dateString = dateFormatter.format(currentDate);
		
		listReportItems.add(new ReportItem(dateString));
		
		do {
			if (reportType.equals(ReportType.DAY)) {
				startDate.add(Calendar.DAY_OF_MONTH, 1);
			} else if (reportType.equals(ReportType.MONTH)) {
				startDate.add(Calendar.MONTH, 1);
			}
			
			currentDate = startDate.getTime();
			dateString = dateFormatter.format(currentDate);
			listReportItems.add(new ReportItem(dateString));
		} while (startDate.before(endDate));
		
		return listReportItems;		
	}

	private void printRawData(List<Order> listOrders) {
		listOrders.forEach(order -> System.out.printf("%-3d | %s | %10.2f | %10.2f \n",
				order.getId(), order.getOrderTime(), order.getTotal(), order.getProductCost()));
	}
}
