/**
 * 
 */
//~`!@#$%^&*()-_+={}[]:|\;"<,>.?/

//For OrgRegister.js


/**
 * Validates username field
 * No Special characters at start and end 
 * No number at start of username
 * return true if all validation are are passed or else error message is returned
 */
function validateUsername() {
	let username = $("#inputUsername").val().trim();
	let spCharAtStart = new RegExp("^(?![!#$%&'*+/=?^_`{|}~])[a-zA-Z0-9]");
	let spCharAtEnd = new RegExp("(?<![!#$%&'*+/=?^_`{|}~])$");
	let numAtStartPattern = new RegExp("^[0-9]{1}");
	
	try {
		if(username === "") {
			return "Username cannot be blank";
		} else if (!spCharAtStart.test(username)) {
			return "Username cannot start with special characters";
		} else if (!spCharAtEnd.test(username)) {
			return "Username cannot end with special characters";
		} else if (username.length > 31) {
			return "Username must be less than or equal to 30 characters"
		} else if(numAtStartPattern.test(username)){
			return "Usernme cannot start with numeric";
		} else {
			return true;
		}
	}catch (e) {
		//console.log(e);
	}
}

function validateWorkEmail() {
	let workEmail = $("#inputEmail").val().trim();
	let regExpPattern = new RegExp(/^(?!\d)[A-Za-z][\w.-]*@skinternational\.[a-zA-Z]{2,}$/);
	
	if(workEmail === "") {
		return "Work Email cannot be blank";
	} else if(!(regExpPattern.test(workEmail))) {
		return "Please enter a valid email address ";
	} else if(workEmail.length > 320) {
		return "Email address max length 320 characters";
	}
	else {
		return true;
	}
}

function validatePassword() {
	let userPasscode = $("#inputPasscode").val().trim();
	let passRegExp = new RegExp(/^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+{}\[\]:;"'.,<>?/~`|\\-])(?!.*\s).{10,}$/);
	if(userPasscode === "") {
		return "Password cannot be blank";
	} else if(!passRegExp.test(userPasscode)) {
		return "password must contain one upper-case, special character and numeric and must be atleast 10 characters";
	} else {
		return true;
	}
}

function validateConfirmPassword() {
	let userConfPasscode = $("#inputConfPasscode").val().trim();
	let userPasscode = $("#inputPasscode").val().trim();
	if(userConfPasscode === "") {
		return "Confirm Password cannot be blank";
	} else if(userConfPasscode !== userPasscode) {
		return "Password and Confirm Password not matching";
	} else {
		return true;
	}
}

function validateData() {
	let currScollPos = document.documentElement.scrollTop;
	document.documentElement.scrollTop = currScollPos;
	
	let usernameRes = validateUsername();
	let workEmailRes = validateWorkEmail();
	let passCodeRes = validatePassword();
	let confPassCodeRes = validateConfirmPassword();
	
	//For username validation
	if( usernameRes != true) {
		$("#inputUsername").css("border-color", "red");
		$(".invalid_username").text(usernameRes);
	} else {
		$("#inputUsername").css("border-color", "#ced4da");
		$(".invalid_username").text("");
	}
	
	// for workemail validation
	if( workEmailRes != true) {
		$("#inputEmail").css("border-color", "red");
		$(".invalid_email").text(workEmailRes);
	} else {
		$("#inputEmail").css("border-color", "#ced4da");
		$(".invalid_email").text("");
	}
	
	// for password validation
	if( passCodeRes != true) {
		$("#inputPasscode").css("border-color", "red");
		$(".invalid_password").text(passCodeRes);
	} else {
		$("#inputPasscode").css("border-color", "#ced4da");
		$(".invalid_password").text("");
	}
	
	// for confirm password validation
	if( confPassCodeRes != true) {
		$("#inputConfPasscode").css("border-color", "red");
		$("#inputPasscode").css("border-color", "red");
		$(".invalid_conf_password").text(confPassCodeRes);
	} else {
		$("#inputConfPasscode").css("border-color", "#ced4da");
		$("#inputPasscode").css("border-color", "#ced4da");
		$(".invalid_conf_password").text("");
	}
	
	return false;
}


$(document).ready(function(){
	
	
	
});