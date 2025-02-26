// Sales Report Common
let MILLISECONDS_A_DAY = 24 * 60 * 60 * 1000;

function setupButtonEventHandlers(reportType, callbackFunction) {
	$(".button-sales-by" + reportType).on("click", function() {
		$(".button-sales-by" + reportType).each(function() {
			$(this).removeClass('btn-primary').addClass('btn-light');
		});
		
		$(this).removeClass('btn-light').addClass('btn-primary');

		let period = $(this).attr("period");
		if (period) {
			callbackFunction(period);
			$("#divCustomDateRange" + reportType).addClass("d-none");
		} else {
			$("#divCustomDateRange" + reportType).removeClass("d-none");
		}		
	});
	
	initCustomDateRange(reportType);
	
	$("#buttonViewReportByDateRange" + reportType).on("click", function() {
		validateDateRange(reportType, callbackFunction);
	});	
}

function validateDateRange(reportType, callbackFunction) {
	let startDateField = document.getElementById('startDate' + reportType);
	let days = calculateDays(reportType);
	
	startDateField.setCustomValidity("");
	
	if (days >= 7 && days <= 30) {
		callbackFunction("custom");
	} else {
		startDateField.setCustomValidity("Dates must be in the range of 7..30 days");
		startDateField.reportValidity();
	}
}

function calculateDays(reportType) {
	let startDateField = document.getElementById('startDate' + reportType);
	let endDateField = document.getElementById('endDate' + reportType);
	let startDate = startDateField.valueAsDate;
	let endDate = endDateField.valueAsDate;
	let differenceInMilliseconds = endDate - startDate;
	return differenceInMilliseconds / MILLISECONDS_A_DAY;
}
	
function initCustomDateRange(reportType) {
	let startDateField = document.getElementById('startDate' + reportType);
	let endDateField = document.getElementById('endDate' + reportType);
	let toDate = new Date();
	endDateField.valueAsDate = toDate;
	let fromDate = new Date();
	fromDate.setDate(toDate.getDate() - 30);
	startDateField.valueAsDate = fromDate;
}	

function formatCurrency(amount) {
	let formattedAmount = $.number(amount, decimalDigits, decimalPointType, thousandsPointType);
	return prefixCurrencySymbol + formattedAmount + suffixCurrencySymbol;
}

function getChartTitle(period) {
	if (period === "last_7_days") return "Sales in Last 7 Days";
	if (period === "last_28_days") return "Sales in Last 28 Days";
	if (period === "last_6_months") return "Sales in Last 6 Months";
	if (period === "last_year") return "Sales in Last Year";
	if (period === "custom") return "Custom Date Range";
	
	return "";
}

function getDenominator(period, reportType) {
	if (period === "last_7_days") return 7;
	if (period === "last_28_days") return 28;
	if (period === "last_6_months") return 6;
	if (period === "last_year") return 12;
	if (period === "custom") return calculateDays(reportType);
	
	return 7;
}

function setSalesAmount(period, reportType, labelTotalItems) {
	$("#textTotalGrossSales" + reportType).text(formatCurrency(totalGrossSales));
	$("#textTotalNetSales" + reportType).text(formatCurrency(totalNetSales));

	let denominator = getDenominator(period, reportType);
	
	$("#textAvgGrossSales" + reportType).text(formatCurrency(totalGrossSales / denominator));
	$("#textAvgNetSales" + reportType).text(formatCurrency(totalNetSales / denominator));
	$("#labelTotalItems" + reportType).text(labelTotalItems);
	$("#textTotalItems" + reportType).text(totalItems);	
}

function formatChartData(data, columnIndex1, columnIndex2) {
	let formatter = new google.visualization.NumberFormat({
		prefix: prefixCurrencySymbol,
		suffix: suffixCurrencySymbol,
		decimalSymbol: decimalPointType,
		groupingSymbol: thousandsPointType,
		fractionDigits: decimalDigits
	});

	formatter.format(data, columnIndex1);
	formatter.format(data, columnIndex2);	
}