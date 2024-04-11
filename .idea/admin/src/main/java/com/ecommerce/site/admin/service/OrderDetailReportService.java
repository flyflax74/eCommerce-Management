package com.ecommerce.site.admin.service;

import com.ecommerce.site.admin.model.ReportItem;
import com.ecommerce.site.admin.model.entity.OrderDetail;
import com.ecommerce.site.admin.model.enums.ReportType;
import com.ecommerce.site.admin.repository.OrderDetailRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class OrderDetailReportService extends AbstractReportService {

	@Autowired
	private OrderDetailRepository repository;

	@Override
	protected List<ReportItem> getReportDataByDateRangeInternal(Date startDate, Date endDate, @NotNull ReportType reportType) {
		List<OrderDetail> listOrderDetails;

		switch (reportType) {
			case CATEGORY -> listOrderDetails = repository.findWithCategoryAndTimeBetween(startDate, endDate);
			case BRAND -> listOrderDetails = repository.findWithBrandAndTimeBetween(startDate, endDate);
			case PRODUCT -> listOrderDetails = repository.findWithProductAndTimeBetween(startDate, endDate);
			default -> listOrderDetails = null;
		}

//		printRawData(listOrderDetails);
		
		List<ReportItem> listReportItems = new ArrayList<>();

		for (OrderDetail detail : listOrderDetails) {
			String identifier;

			switch (reportType) {
				case CATEGORY -> identifier = detail.getProduct().getCategory().getName();
				case BRAND -> identifier = detail.getProduct().getBrand().getName();
				case PRODUCT -> identifier = detail.getProduct().getShortName();
				default -> identifier = "";
			}

			ReportItem reportItem = new ReportItem(identifier);
			float grossSales = detail.getSubtotal() + detail.getShippingCost();
			float netSales = detail.getSubtotal() - detail.getProductCost();
			int itemIndex = listReportItems.indexOf(reportItem);

			if (itemIndex >= 0) {
				reportItem = listReportItems.get(itemIndex);
				reportItem.addGrossSales(grossSales);
				reportItem.addNetSales(netSales);
				reportItem.increaseProductsCount(detail.getQuantity());
			} else {
				listReportItems.add(new ReportItem(identifier, grossSales, netSales, detail.getQuantity()));
			}
		}

//		printReportData(listReportItems);
		return listReportItems;
	}

	private void printReportData(@NotNull List<ReportItem> listReportItems) {
		for (ReportItem item : listReportItems) {
			System.out.printf("%-20s, %10.2f, %10.2f, %d \n",
					item.getIdentifier(), item.getGrossSales(), item.getNetSales(), item.getProductsCount());
		}
	}

	private void printRawData(@NotNull List<OrderDetail> listOrderDetails) {
		for (OrderDetail detail : listOrderDetails) {
			System.out.printf("%d, %-20s, %10.2f, %10.2f, %10.2f \n",
					detail.getQuantity(), detail.getProduct().getShortName().substring(0, 20),
					detail.getSubtotal(), detail.getProductCost(), detail.getShippingCost());
		}
	}
}
