/**
 * @author Binson Selvin
 */

$(document).ready(function() {
	console.log("DOM ready");

	//hide the alert at start
	if ($(".alert-danger").text() !== '') {
		$(".alert-danger").show();
		fadeoutBoxes();
	} else {
		$(".alert-danger").hide();
	}


	function fadeoutBoxes() {
		$(".alert-danger").show().delay(15000).fadeOut();
	}

	$(".invalid-feedback").hide();

	$("#username").on("focusout", function() {
		if ($("#username").val() === '') {
			$(".invalid-feedback").text("Username field cannot be empty");
			$(".invalid-feedback").show();
			$("#username").css({ "border-color": "red" });
		} else {
			$(".invalid-feedback").text("");
			$(".invalid-feedback").hide();
			$("#username").css({ "border-color": "#cfe8fd" });
		}
	});

	$("#captcha").on("change", function() {
		alert("Hii");
		var response = grecaptcha.getResponse();
		if (response.length === 0) {
			$(".invalid-captcha").text("*Please verify captcha");
			$(".invalid-captcha").show();
			$("#username").css({ "border-color": "red" });
		} else {
			$(".invalid-captcha").text("");
			$(".invalid-captcha").hide();
			$("#username").css({ "border-color": "#cfe8fd" });
		}
	});

	$("#password").on("focusout", function() {
		if ($("#password").val() === '') {
			$(".invalid-feedback").text("Password field cannot be empty");
			$(".invalid-feedback").show();
			$("#password").css({ "border-color": "red" });
		} else {
			$(".invalid-feedback").text("");
			$(".invalid-feedback").hide();
			$("#password").css({ "border-color": "#cfe8fd" });
		}
	});


});


/*
* Validate the form before submitting to the server
*/
var captchaClicked = false;

function validateLogin(e) {
	console.log("validating forms...");
	var response = grecaptcha.getResponse();
	//$(".form").addClass("alt");
	var eleLst = document.querySelectorAll(".req");
	let validationPassed = true;

	eleLst.forEach(element => {
		if (element.value === '' && validationPassed) {
			let elementLabel = element.placeholder;
			let elementId = element.id;
			console.log(elementLabel);
			$(".form").addClass("alt");
			$(".invalid-feedback").text(elementLabel + " field cannot be empty");
			$(".invalid-feedback").show();
			$("#" + elementId).css({ "border-color": "red" });
			validationPassed = false;
		} else {
			console.log("in else: " + element.placeholder);
			$(".form").removeClass("alt");
			validationPassed = true;
		}
	});

	let element = document.getElementById("form");

	if (element.classList.contains("alt")) {
		//prevent the default event
		//e.preventDefault();
		console.log("alt class");
		return false;
	} else {
		if(response.length==0 && !captchaClicked) {
			$(".invalid-captcha").text("*Please verify captcha");
			$(".invalid-captcha").show();
			return false;
		} else {
			console.log("not alt class");
			$(".form").removeClass("alt");
			return true;	
		}
	}
}

	function validateCaptcha() {
		console.log("Captcha clicked");
		$(".invalid-captcha").text("");
		$(".invalid-captcha").hide();
		captchaClicked = true;
	}
	
	function captchaExpired() {
		console.log("Captcha expired");
		$(".invalid-captcha").text("Please verify captcha");
		$(".invalid-captcha").show();
		captchaClicked = false;
	}
	
	
