let dataByCategory;
let chartOptionsByCategory;

$(document).ready(function() {
	setupButtonEventHandlers("_category", loadSalesReportByDateForCategory);	
});

function loadSalesReportByDateForCategory(period) {
	let startDate;
	let endDate;
	let requestUrl;
	if (period === "custom") {
		startDate = $("#startDate_category").val();
		endDate = $("#endDate_category").val();
		requestUrl = contextPath + "reports/category/" + startDate + "/" + endDate;
	} else {
		requestUrl = contextPath + "reports/category/" + period;
	}
	
	$.get(requestUrl, function(responseJSON) {
		prepareChartDataForSalesReportByCategory(responseJSON);
		customizeChartForSalesReportByCategory();
		formatChartData(dataByCategory, 1, 2);
		drawChartForSalesReportByCategory(period);
		setSalesAmount(period, '_category', "Total Products");
	});
}

function prepareChartDataForSalesReportByCategory(responseJSON) {
	dataByCategory = new google.visualization.DataTable();
	dataByCategory.addColumn('string', 'Category');
	dataByCategory.addColumn('number', 'Gross Sales');
	dataByCategory.addColumn('number', 'Net Sales');
	
	totalGrossSales = 0.0;
	totalNetSales = 0.0;
	totalItems = 0;
	
	$.each(responseJSON, function(index, reportItem) {
		dataByCategory.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales]]);
		totalGrossSales += parseFloat(reportItem.grossSales);
		totalNetSales += parseFloat(reportItem.netSales);
		totalItems += parseInt(reportItem.productsCount);
	});
}

function customizeChartForSalesReportByCategory() {
	chartOptionsByCategory = {
		height: 360, legend: {position: 'right'}
	};
}

function drawChartForSalesReportByCategory() {
	let salesChart = new google.visualization.PieChart(document.getElementById('chart_sales_by_category'));
	salesChart.draw(dataByCategory, chartOptionsByCategory);
}