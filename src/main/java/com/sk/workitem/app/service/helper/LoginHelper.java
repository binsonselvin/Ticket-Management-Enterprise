package com.sk.workitem.app.service.helper;

import java.util.Base64;
import java.util.regex.Pattern;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class LoginHelper {
	
	/***
	 * Validates whether entered email is valid or not
	 * @param email {@link String} email of the user 
	 * @return true if valid email or else false
	 */
	public static boolean validateEmailAddress(String email) {
		if(!Pattern.compile("^(?!\\d)[A-Za-z][\\w.-]*@skinternational\\.[a-zA-Z]{2,}$").matcher(email).find()) {
			return false;
		}
		return true;
	}
}
