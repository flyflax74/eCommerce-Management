let dataByBrand;
let chartOptionsByBrand;

$(document).ready(function() {
    setupButtonEventHandlers("_brand", loadSalesReportByDateForBrand);
});

function loadSalesReportByDateForBrand(period) {
    let startDate;
    let endDate;
    let requestUrl;
    if (period === "custom") {
        startDate = $("#startDate_brand").val();
        endDate = $("#endDate_brand").val();
        requestUrl = contextPath + "reports/brand/" + startDate + "/" + endDate;
    } else {
        requestUrl = contextPath + "reports/brand/" + period;
    }

    $.get(requestUrl, function(responseJSON) {
        prepareChartDataForSalesReportByBrand(responseJSON);
        customizeChartForSalesReportByBrand();
        formatChartData(dataByBrand, 1, 2);
        drawChartForSalesReportByBrand(period);
        setSalesAmount(period, '_brand', "Total Products");
    });
}

function prepareChartDataForSalesReportByBrand(responseJSON) {
    dataByBrand = new google.visualization.DataTable();
    dataByBrand.addColumn('string', 'Brand');
    dataByBrand.addColumn('number', 'Gross Sales');
    dataByBrand.addColumn('number', 'Net Sales');

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    $.each(responseJSON, function(index, reportItem) {
        dataByBrand.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalItems += parseInt(reportItem.productsCount);
    });
}

function customizeChartForSalesReportByBrand() {
    chartOptionsByBrand = {
        height: 360, legend: {position: 'right'}
    };
}

function drawChartForSalesReportByBrand() {
    let salesChart = new google.visualization.PieChart(document.getElementById('chart_sales_by_brand'));
    salesChart.draw(dataByBrand, chartOptionsByBrand);
}