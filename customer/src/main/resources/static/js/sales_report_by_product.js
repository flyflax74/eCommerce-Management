// Sales Report by Product
let dataByProduct;
let chartOptionsByProduct;

$(document).ready(function() {
	setupButtonEventHandlers("_product", loadSalesReportByDateForProduct);	
});

function loadSalesReportByDateForProduct(period) {
	let requestURL;
	let endDate;
	let startDate;
	if (period === "custom") {
		startDate = $("#startDate_product").val();
		endDate = $("#endDate_product").val();

		requestURL = contextPath + "reports/product/" + startDate + "/" + endDate;
	} else {
		requestURL = contextPath + "reports/product/" + period;
	}
	
	$.get(requestURL, function(responseJSON) {
		prepareChartDataForSalesReportByProduct(responseJSON);
		customizeChartForSalesReportByProduct();
		formatChartData(dataByProduct, 2, 3);
		drawChartForSalesReportByProduct(period);
		setSalesAmount(period, '_product', "Total Products");
	});
}

function prepareChartDataForSalesReportByProduct(responseJSON) {
	dataByProduct = new google.visualization.DataTable();
	dataByProduct.addColumn('string', 'Product');
	dataByProduct.addColumn('number', 'Quantity');
	dataByProduct.addColumn('number', 'Gross Sales');
	dataByProduct.addColumn('number', 'Net Sales');
	
	totalGrossSales = 0.0;
	totalNetSales = 0.0;
	totalItems = 0;
	
	$.each(responseJSON, function(index, reportItem) {
		dataByProduct.addRows([[reportItem.identifier, reportItem.productsCount, reportItem.grossSales, reportItem.netSales]]);
		totalGrossSales += parseFloat(reportItem.grossSales);
		totalNetSales += parseFloat(reportItem.netSales);
		totalItems += parseInt(reportItem.productsCount);
	});
}

function customizeChartForSalesReportByProduct() {
	chartOptionsByProduct = {
		height: 360, width: '98%',
		showRowNumber: true,
		page: 'enable',
		sortColumn: 2,
		sortAscending: false
	};
}

function drawChartForSalesReportByProduct() {
	let salesChart = new google.visualization.Table(document.getElementById('chart_sales_by_product'));
	salesChart.draw(dataByProduct, chartOptionsByProduct);
}