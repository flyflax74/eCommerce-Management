package com.ecommerce.site.admin.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ecommerce.site.admin.model.ReportItem;
import com.ecommerce.site.admin.model.enums.ReportType;
import com.ecommerce.site.admin.service.MasterOrderReportService;
import com.ecommerce.site.admin.service.OrderDetailReportService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ReportRestController {

	@Autowired
	private MasterOrderReportService masterOrderReportService;

	@Autowired
	private OrderDetailReportService orderDetailReportService;
	
	@GetMapping("/reports/sales_by_date/{period}")
	public List<ReportItem> getReportDataByDatePeriod(@PathVariable("period") @NotNull String period) {
		System.out.println("Report period: " + period);

		return switch (period) {
			case "last_28_days" -> masterOrderReportService.getReportDataLast28Days(ReportType.DAY);
			case "last_6_months" -> masterOrderReportService.getReportDataLast6Months(ReportType.MONTH);
			case "last_year" -> masterOrderReportService.getReportDataLastYear(ReportType.MONTH);
			default -> masterOrderReportService.getReportDataLast7Days(ReportType.DAY);
		};
	}
	
	@GetMapping("/reports/sales_by_date/{startDate}/{endDate}")
	public List<ReportItem> getReportDataByDatePeriod(@PathVariable("startDate") String startDate,
													  @PathVariable("endDate") String endDate) throws ParseException {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse(startDate);
		Date endTime = dateFormatter.parse(endDate);
		return masterOrderReportService.getReportDataByDateRange(startTime, endTime, ReportType.DAY);
	}
	
	@GetMapping("/reports/{groupBy}/{startDate}/{endDate}")
	public List<ReportItem> getReportDataByCategoryOrProductDateRange(@PathVariable("groupBy") @NotNull String groupBy,
																	  @PathVariable("startDate") String startDate,
																	  @PathVariable("endDate") String endDate) throws ParseException {
		ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse(startDate);
		Date endTime = dateFormatter.parse(endDate);
		return orderDetailReportService.getReportDataByDateRange(startTime, endTime, reportType);
	}
	
	@GetMapping("/reports/{groupBy}/{period}")
	public List<ReportItem> getReportDataByCategoryOrProduct(@PathVariable("groupBy") @NotNull String groupBy,
															 @PathVariable("period") @NotNull String period) {
		ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
		return switch (period) {
			case "last_28_days" -> orderDetailReportService.getReportDataLast28Days(reportType);
			case "last_6_months" -> orderDetailReportService.getReportDataLast6Months(reportType);
			case "last_year" -> orderDetailReportService.getReportDataLastYear(reportType);
			default -> orderDetailReportService.getReportDataLast7Days(reportType);
		};
	}
}
