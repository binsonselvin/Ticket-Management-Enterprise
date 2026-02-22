
//Global variable for timer
var timer = 30;

function startOtpTimer() {
	if(timer >= 0) {
		$(".otp-timer").text("in "+timer+"s");
		timer = timer - 1;
	}
	if(timer <= -1) {
		$("#resend_otp").css("color","#0d6efd");
		$("#resend_otp").css("color","#0d6efd");
		$(".otp-timer").text("");
	}
}

function resendOtp() {
	console.log("timer: "+timer);
	if(timer === -1) {
		timer = 30;
		setInterval(startOtpTimer, 1000);
		console.log("ajax call for otp");
		/**
		 * Execute Ajax call for new OTP
		 */
	} else {
		return false;
	}
}

$(document).ready(function() {
	setInterval(startOtpTimer, 1000);
});