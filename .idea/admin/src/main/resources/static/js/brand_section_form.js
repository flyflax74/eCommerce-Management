let chosenBrandsDropDown;
let allBrandsDropDown;

$(document).ready(function() {
	chosenBrandsDropDown = $('#chosenBrands');
	allBrandsDropDown = $('#brands');
		
	$("#buttonAddBrand").on("click", function() {
		addSelectedBrand();
	});
	$("#linkRemoveBrand").on("click", function(e) {
		e.preventDefault();
		removeAChosenBrand();
	});
	$("#linkMoveBrandUp").on("click", function(e) {
		e.preventDefault();
		moveAChosenBrandUp();
	});
	$("#linkMoveBrandDown").on("click", function(e) {
		e.preventDefault();
		moveAChosenBrandDown();
	});	
});

function addSelectedBrand() {
	allBrandsDropDown.children('option:selected').each(function() {
		let selectedBrand = $(this);
		let brandId = selectedBrand.val();
		let brandName = selectedBrand.text();
		let dropdownChosenBrands = $('#chosenBrands');
		if (!isBrandAdded(brandId)) {
			$("<option>").val(brandId + "-0").text(brandName).appendTo(dropdownChosenBrands);
		}
	});		
}

function isBrandAdded(brandId) {
	let isAdded = false;
	chosenBrandsDropDown.children('option').each(function() {
		let chosenBrand = $(this);
		let chosenBrandId = chosenBrand.val().split("-")[0];
		if (brandId === chosenBrandId) {
			isAdded = true;
		}
	});
	return isAdded;
}

function removeAChosenBrand() {
	let chosenBrandId = chosenBrandsDropDown.val();
	$("#chosenBrands option[value='" + chosenBrandId + "']").remove();
}

function moveAChosenBrandUp() {
	let selectedChosenBrand = $("#chosenBrands option:selected");
	let chosenBrandAbove;
	if (selectedChosenBrand != null) {
		chosenBrandAbove = selectedChosenBrand.prev();
		selectedChosenBrand.insertBefore(chosenBrandAbove);
	}
}

function moveAChosenBrandDown() {
	let selectedChosenBrand = $("#chosenBrands option:selected");
	let chosenBrandBelow;
	if (selectedChosenBrand != null) {
		chosenBrandBelow = selectedChosenBrand.next();
		selectedChosenBrand.insertAfter(chosenBrandBelow);
	}	
}

function processBeforeSubmit() {
	chosenBrandsDropDown.children('option').each(function() {
		$(this).prop('selected', true);
	});
}