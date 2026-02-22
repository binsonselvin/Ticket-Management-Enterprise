package com.sk.workitem.app.service.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sk.workitem.app.errors.RegistrationError;
import com.sk.workitem.app.model.MasterOrganization;

public class RegistrationHelper {
	
	/**
	 * Receives inputUsername data from OrgRegister.html form and checks for any validation failure if validation fails then
	 * {@link Map<String, Object>} with field name and {@link RegistrationError} is populated and returned
	 * @param inputUsername {@link String} username of the user
	 * @return  {@link Map<String, Object>} the object may be an error object or empty  {@link Map<String, Object>}
	 */
	public static Map<String, String> validateUsername(String inputUsername) {
		
		Map<String, String> resultMap = new HashMap<>();
		String fieldName = "inputUsernameErr";
		
		if(inputUsername != null) {
			if(inputUsername.equals("")) {
				resultMap.put(fieldName, "Username cannot be blank");
				return resultMap;
			}
			// Check Special Character at start
			if(!Pattern.compile("^(?![!#$%&'*+/=?^_`{|}~])[a-zA-Z0-9]").matcher(inputUsername).find()) {
				resultMap.put(fieldName, "Username cannot start with special characters");
				return resultMap;
			}

			// Check Special Character at end
			if(!Pattern.compile("(?<![!#$%&'*+/=?^_`{|}~])$").matcher(inputUsername).find()) {
				resultMap.put(fieldName, "Username cannot end with special characters");
				return resultMap;
			}

			// username must be greater than 1 characters
			if(inputUsername.length() < 2) {
				resultMap.put(fieldName, "Username must be greater than 1 characters");
				return resultMap;
			}
			

			// Username must be less than or equal to 30 characters
			if(inputUsername.length() > 30) {
				resultMap.put(fieldName, "Username must be less than or equal to 30 characters");
				return resultMap;
			}

			// Check Numeric at start
			if(Pattern.compile("^[0-9]{1}").matcher(inputUsername).find()) {
				resultMap.put(fieldName, "Username cannot start with numeric");
				return resultMap;
			}
			
		} else {
			resultMap.put(fieldName, "Username cannot be blank");
			return resultMap;
		}
		return resultMap;
	}
	
	/**
	 * Receives inputEmail data from OrgRegister.html form and checks for any validation failure if validation fails then
	 * {@link Map<String, Object>} with field name and {@link RegistrationError} is populated and returned
	 * @param inputUsername {@link String} username of the user
	 * @return  {@link Map<String, Object>} the object may be an error object or empty  {@link Map<String, Object>}
	 */
	public static Map<String, String> validateEmail(String inputEmail) {
		Map<String, String> resultMap = new HashMap<>();
		String fieldName = "inputEmailErr";
		
		if(inputEmail != null) {
			if(inputEmail.equals("")) {
				resultMap.put(fieldName, "Work Email cannot be blank");
				return resultMap;
			}
			
			if(inputEmail.length() > 320) {
				resultMap.put(fieldName, "Email address max length 320 characters");
				return resultMap;
			}
			
			// Check for valid skinternational email
			if(!Pattern.matches("^(?!\\d)[A-Za-z][\\w.-]*@skinternational\\.[a-zA-Z]{2,}$", inputEmail)) {
				resultMap.put(fieldName, "Please enter a valid email address");
				return resultMap;
			}
		} else {
			resultMap.put(fieldName, "Work Email cannot be blank");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * Receives inputPassword data from OrgRegister.html form and checks for any validation failure if validation fails then
	 * {@link Map<String, Object>} with field name and {@link RegistrationError} is populated and returned
	 * @param inputUsername {@link String} username of the user
	 * @return  {@link Map<String, Object>} the object may be an error object or empty  {@link Map<String, Object>}
	 */
	public static Map<String, String> validatePassword(String inputPassword) {
		Map<String, String> resultMap = new HashMap<>();
		String fieldName = "inputPasscodeErr";
		
		if(inputPassword != null) {
			if(inputPassword.equals("")) {
				resultMap.put(fieldName, "Password cannot be blank");
				return resultMap;
			}
			
			// Check for valid skinternational email
			if(!Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}\\[\\]:;\"'.,<>?/~`|\\\\-])(?!.*\\s).{10,}$").matcher(inputPassword).find()) {
				resultMap.put(fieldName, "password must contain one upper-case, special character and numeric and must be atleast 10 characters");
				return resultMap;
			}
			
		} else {
			resultMap.put(fieldName, "Password cannot be blank");
			return resultMap;
		}
		return resultMap;
	}
	
	/***
	 * Validates the confirm password and also checks passCode is equal to confPasscode
	 * @param passCode {@link String} password entered by the user
	 * @param confPassCode {@link String} confirm password entered by the user
	 * @return {@link Map<String, Object>} the object may be an error object or empty  {@link Map<String, Object>}
	 */
	public static Map<String, String> validateConfirmPassword(String passCode, String confPassCode) {
		Map<String, String> resultMap = new HashMap<>();
		String errorLbl = "confPassError";
		
		if(Objects.isNull(confPassCode)) {
			resultMap.put(errorLbl, "Confirm Password cannot be blank");
		} else {
			if(confPassCode.equals("")) {
				resultMap.put(errorLbl, "Confirm Password cannot be blank");
			} else if(!passCode.equals(confPassCode)) {
				resultMap.put(errorLbl, "Password and Confirm Password not matching");
			}
		}
		return resultMap;
	}
}
