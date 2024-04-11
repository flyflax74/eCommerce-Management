let chosenArticlesDropDown;
let allArticlesDropDown;

$(document).ready(function() {
	chosenArticlesDropDown = $('#chosenArticles');
	allArticlesDropDown = $('#articles');
		
	$("#buttonAddArticle").on("click", function() {
		addSelectedArticle();
	});
	
	$("#linkRemoveArticle").on("click", function(e) {
		e.preventDefault();
		removeAChosenArticle();
	});
	
	$("#linkMoveArticleUp").on("click", function(e) {
		e.preventDefault();
		moveAChosenArticleUp();
	});		
	
	$("#linkMoveArticleDown").on("click", function(e) {
		e.preventDefault();
		moveAChosenArticleDown();
	});	
});

function addSelectedArticle() {
	allArticlesDropDown.children('option:selected').each(function() {
		let selectedArticle = $(this);
		let articleId = selectedArticle.val();
		let articleTitle = selectedArticle.text();
		let dropdownChosenArticles = $('#chosenArticles');
		
		if (!isArticleAdded(articleId)) {
			$("<option>").val(articleId + "-0").text(articleTitle).appendTo(dropdownChosenArticles);
		}
	});		
}

function isArticleAdded(articleId) {
	let isAdded = false;
	
	chosenArticlesDropDown.children('option').each(function() {
		let chosenArticle = $(this);
		let chosenArticleId = chosenArticle.val().split("-")[0];

		if (articleId === chosenArticleId) {
			isAdded = true;

		}
	});
	
	return isAdded;
}

function removeAChosenArticle() {
	let chosenArticleId = chosenArticlesDropDown.val();

	$("#chosenArticles option[value='" + chosenArticleId + "']").remove();
}

function moveAChosenArticleUp() {
	let selectedChosenArticle = $("#chosenArticles option:selected");
	let chosenArticleAbove;
	if (selectedChosenArticle != null) {
		chosenArticleAbove = selectedChosenArticle.prev();
		selectedChosenArticle.insertBefore(chosenArticleAbove);
	}
}

function moveAChosenArticleDown() {
	let selectedChosenArticle = $("#chosenArticles option:selected");
	let chosenArticleBelow;
	if (selectedChosenArticle != null) {
		chosenArticleBelow = selectedChosenArticle.next();
		selectedChosenArticle.insertAfter(chosenArticleBelow);
	}	
}

function processBeforeSubmit() {
	chosenArticlesDropDown.children('option').each(function() {
		$(this).prop('selected', true);
	});

}