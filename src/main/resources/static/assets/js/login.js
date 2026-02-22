	function validateData() {
		alert(validatingEmail());
		if(validatingEmail() && validatingPasscode()) {
			return true;
		} else {
			return false;
		}
	}
	
	function validatingEmail() {
		let workEmail = $("#user_email").val();
		let regExpPattern = new RegExp(/^(?!\d)[A-Za-z][\w.-]*@skinternational\.[a-zA-Z]{2,}$/);
		if(workEmail==="") {
			$(".invalid-email").text("Email adddress cannot be blank");
			return false;
		} else if(!(regExpPattern.test(workEmail))) {
			$(".invalid-email").text("Please enter a valid email address");
			$("#user_email").css("border-color", "red");
			return false;
		} else if(workEmail.length > 320) {
			$(".invalid-email").text("Email address max length 320 characters");
			return false;
		}
		return true;
	}
	
	function validatingPasscode() {
		alert("returning false");
		let passcode = $("#user_password").val();
		if(passcode==="") {
			$(".invalid-password").text("Password cannot be blank");
			$("#user_password").css("border-color", "red");
			
			return false;
		} else {
			$("#user_password").css("border-color", "#ced4da");
		}
		return true;
	}

$(document).ready(function(){
	
});