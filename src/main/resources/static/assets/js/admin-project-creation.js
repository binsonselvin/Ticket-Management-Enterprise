/**
 * @author Binson Selvin
 * @since 24-09-2024
 */

/**
 * get context-path of the application
 */
function getContextPath() {
	return window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
}

$(document).ready(function() {
	let relatedCustomerList = [];
	let relatedActivityList = [];
	let relatedManagerList = [];

	//remove --None-- from multiselect picklist
	removeSelectOptTmp();
	checkForServerSideError();
	
	//check for serverside error for custom picklist
	function checkForServerSideError() {
		if($("#activity-error").text() !== "") {
			alert("activity server side error");
			$('.activityDiv').find('button:first').css("border-color", "red");
			$("#activity-error").text("Please select activity");
		}
	}

	$("#customerGroup").on("change", function() {
		let customerGroupVal = $("#customerGroup").val();
		
		let contextPath = getContextPath();
		let serverRequestUrl = "admin/project/relatedCustomer"

		$.ajax({
			url: contextPath + "/" + serverRequestUrl,
			async: false,
			data: {
				"customerGroup": customerGroupVal
			},
			success: function(res) {
				console.log(res);
				console.log(res.objData.dataMap);

				let options = $("#customer-name-select");

				//delete previous options if exists
				options
					.find('option')
					.remove()
					.end()
					.append('<option style="cursor: not-allowed;" disabled selected>--None--</option>');

				if (relatedCustomerList.length > 0) {
					for (const element of relatedCustomerList) {
						options
							.find('option')
							.remove()
							.end()
							.append('<option style="cursor: not-allowed;" disabled selected>--None--</option>');
					}
				}
				
				relatedCustomerList = res.objData.dataMap;
				for (const element of relatedCustomerList) {
					options.append($("<option />").val(element.customerId).text(element.customerName));
				}
			},
			error: function(err) {
				console.log(err);
				let options = $("#customer-name-select");
				for (const element of relatedCustomerList) {
					options
						.find('option')
						.remove()
						.end()
						.append('<option style="cursor: not-allowed;" disabled selected>--None--</option>');
				}
			}
		});
	});

	$("#projectType").on("change", function() {
		let projectTypeVal = $("#projectType").val();
		$("#projectActivityValue").val("");
		if(projectTypeVal !== null) {
			$("#projectType").css("border-color", "#E8E9E9");
			$("#project-type-error").text("");
		}
		
		let contextPath = getContextPath();
		let serverRequestUrl = "admin/project/relatedActivity"

		$.ajax({
			url: contextPath + "/" + serverRequestUrl,
			async: false,
			data: {
				"projectType": projectTypeVal
			},
			success: function(res) {
				console.log(res);
				console.log(res.objData.dataMap);
				let options = $("#activity-select");
				if (relatedActivityList.length > 0) {
					$('#activity-select option').remove();
					$("#activity-select").selectpicker('refresh');
				}
				relatedActivityList = res.objData.dataMap;

				for (const element of relatedActivityList) {
					options.append($("<option />").val(element.activityId).text(element.activityName));
				}
				//refresh the select
				$("#activity-select").selectpicker('refresh');
			},
			error: function(err) {
				console.log(err);
				removeSelectOptTmp();
			}
		});
	});

	//for customer picklist change event
	$("#customer-name-select").on("change", function() {
		if($("#customer-name-select").val() !== null) {
			$("#customer-name-select").css("border-color", "#E8E9E9");
			$("#customer-name-error").text("");
		}
	});

	$("#activity-select").on("change", function() {
		if($("#activity-select").val() !== null) {
			let selectedVal = $(this).val();
			$("#projectActivityValue").val(selectedVal+",");
			$('.activityDiv').find('button:first').css("border-color", "#E8E9E9");
			$("#activity-error").text("");
		}
	});
	
	$("#start-date").on("change", function() {
		if ($("#start-date").val() !== "") {
			$("#start-date").css("border-color", "#E8E9E9");
			$("#start-date-error").text("");
		}
	});
	
	$("#end-date").on("change", function() {
		if ($("#end-date").val() !== "") {
			$("#end-date").css("border-color", "#E8E9E9");
			$("#end-date-error").text("");
		}
	});
	
	$("#contract-type").on("change", function() {
		if ($("#contract-type").val() !== "") {
			$("#contract-type").css("border-color", "#E8E9E9");
			$("#contract-type-error").text("");
		}
	});
	
	$("#manager-select").on("change", function() {
		if ($("#manager-select").val() !== "") {
			$("#manager-select").css("border-color", "#E8E9E9");
			$("#manager-error").text("");
		}
	});

	/***
	 * for branch select - populate manager based on branch selected
	 */
	$("#branch-select").on("change", function() {
		let selectedBranch = $("#branch-select").val();
		
		if(selectedBranch !== null) {
			$("#branch-select").css("border-color", "#E8E9E9");
			$("#sk-branch-error").text("");
		}
		
		let contextPath = getContextPath();
		let serverRequestUrl = "admin/project/relatedManager";

		$.ajax({
			url: contextPath + "/" + serverRequestUrl,
			async: false,
			data: {
				"branchSelected": selectedBranch
			},
			success: function(res) {
				console.log(res);
				console.log(res.objData.dataMap);
				let options = $("#manager-select");
				//delete previous options
				if (relatedManagerList.length > 0) {
					for (const element of relatedManagerList) {
						options
							.find('option')
							.remove()
							.end()
							.append('<option style="cursor: not-allowed;" disabled selected>--None--</option>');
					}
				}

				relatedManagerList = res.objData.dataMap;

				for (const element of relatedManagerList) {
					options.append($("<option />").val(element.userEmail).text(element.username));
				}
			},
			error: function(err) {
				console.log(err);
				let options = $("#manager-select");
				for (const element of relatedManagerList) {
					options
						.find('option')
						.remove()
						.end()
						.append('<option style="cursor: not-allowed;" disabled selected>--None--</option>');
				}
			}
		});
	});

});


function validateSubmitForm() {
	alert("validating form");
	
	let containError = [];
	//for customer name
	if ($("#customer-name-select").val() == null) {
		$("#customer-name-select").css("border-color", "red");
		$("#customer-name-error").text("Please select customer name");
		containError.push("error");
	} else {
		$("#customer-name-select").css("border-color", "#E8E9E9");
		$("#customer-name-error").text("");
	}
	
	//for project type
	if ($("#projectType").val() == null) {
		$("#projectType").css("border-color", "red");
		$("#project-type-error").text("Please select project/application type");
		containError.push("error");
	} else {
		$("#projectType").css("border-color", "#E8E9E9");
		$("#project-type-error").text("");
	}
	
	//for Activity
	if ($("#activity-select").val() == "") {
		//$(".activityDiv > .btn-light").css("border-color", "red !important");
		$('.activityDiv').find('button:first').css("border-color", "red");
		$("#activity-error").text("Please select activity");
		containError.push("error");
	} else {
		$('.activityDiv').find('.btn-light:first').css("border-color", "#E8E9E9");
		$("#activity-error").text("");
	}
	
	//for Start Date
	if ($("#start-date").val() == "") {
		$("#start-date").css("border-color", "red");
		$("#start-date-error").text("Please select start date");
		containError.push("error");
	} else {
		if($("#start-date").val() < new Date().toISOString().substr(0, 10)) {
			$("#start-date").css("border-color", "red");
			$("#start-date-error").text("Start date cannot be in past");
		} else {
			let startDate = $("#start-date").val();
			$("#start-date").css("border-color", "#E8E9E9");
			$("#start-date-error").text("");	
		}
	}
	
	//for End Date
	if ($("#end-date").val() == "") {
		$("#end-date").css("border-color", "red");
		$("#end-date-error").text("Please select end date");
		containError.push("error");
	} else {
		$("#end-date").css("border-color", "#E8E9E9");
		$("#end-date-error").text("");
	}

	//for contract type
	if ($("#contract-type").val() == null) {
		$("#contract-type").css("border-color", "red");
		$("#contract-type-error").text("Please select contract type");
		containError.push("error");
	} else {
		$("#contract-type").css("border-color", "#E8E9E9");
		$("#contract-type-error").text("");
	}

	//for SK Branch
	if ($("#branch-select").val() == null) {
		$("#branch-select").css("border-color", "red");
		$("#sk-branch-error").text("Please select sk branch");
		containError.push("error");
	} else {
		$("#branch-select").css("border-color", "#E8E9E9");
		$("#sk-branch-error").text("");
	}

	//for Manager 
	if ($("#manager-select").val() == null) {
		$("#manager-select").css("border-color", "red");
		$("#manager-error").text("Please select manager");
		containError.push("error");
	} else {
		$("#manager-select").css("border-color", "#E8E9E9");
		$("#manager-error").text("");
	}

	//compare start date and end date 
	if($("#start-date").val() !== "" && $("#end-date").val()) {
		if($("#start-date").val() > $("#end-date").val()) {
			$("#end-date").css("border-color", "red");
			$("#end-date-error").text("End date cannot be before start date");
			containError.push("error");
		}
	}

	if(containError.length > 0) {
		alert("cannot submit form contains ERROR");
		console.log("cannot submit form contains ERROR");
		return false;
	} else {
		alert("proceed to form submission");
		console.log("proceed to form submission");
		$("#project-creation-form").submit();
		return true;
	}
}

/**
 * removes the --None-- option in from selectpicker
 */
function removeSelectOptTmp() {
	$('#activity-select option').remove();
	$("#activity-select").selectpicker('refresh');
}