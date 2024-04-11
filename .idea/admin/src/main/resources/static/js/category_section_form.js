let chosenCategoriesDropDown;
let allCategoriesDropDown;

$(document).ready(function() {
	chosenCategoriesDropDown = $('#chosenCategories');
	allCategoriesDropDown = $('#categories');
		
	$("#buttonAddCategory").on("click", function() {
		addSelectedCategory();
	});
	$("#linkRemoveCategory").on("click", function(e) {
		e.preventDefault();
		removeAChosenCategory();
	});
	$("#linkMoveCatUp").on("click", function(e) {
		e.preventDefault();
		moveAChosenCategoryUp();
	});
	$("#linkMoveCatDown").on("click", function(e) {
		e.preventDefault();
		moveAChosenCategoryDown();
	});	
});

function addSelectedCategory() {
	allCategoriesDropDown.children('option:selected').each(function() {
		let selectedCategory = $(this);
		let catId = selectedCategory.val();
		let catName = selectedCategory.text().replace(/-/g, "");
		let dropdownChosenCategories = $('#chosenCategories');
		if (!isCategoryAdded(catId)) {
			$("<option>").val(catId + "-0").text(catName).appendTo(dropdownChosenCategories);			
		}
	});		
}

function isCategoryAdded(catId) {
	let isAdded = false;
	
	chosenCategoriesDropDown.children('option').each(function() {
		let chosenCategory = $(this);
		let chosenCatId = chosenCategory.val().split("-")[0];
		if (catId === chosenCatId) {
			isAdded = true;
		}
	});
	return isAdded;
}

function removeAChosenCategory() {
	let chosenCatId = chosenCategoriesDropDown.val();
	$("#chosenCategories option[value='" + chosenCatId + "']").remove();	
}

function moveAChosenCategoryUp() {
	let selectedChosenCategory = $("#chosenCategories option:selected");
	let chosenCategoryAbove;
	if (selectedChosenCategory != null) {
		chosenCategoryAbove = selectedChosenCategory.prev();
		selectedChosenCategory.insertBefore(chosenCategoryAbove);
	}
}

function moveAChosenCategoryDown() {
	let selectedChosenCategory = $("#chosenCategories option:selected");
	let chosenCategoryBelow;
	if (selectedChosenCategory != null) {
		chosenCategoryBelow = selectedChosenCategory.next();
		selectedChosenCategory.insertAfter(chosenCategoryBelow);
	}	
}

function processBeforeSubmit() {
	chosenCategoriesDropDown.children('option').each(function() {
		$(this).prop('selected', true);
	});	
}