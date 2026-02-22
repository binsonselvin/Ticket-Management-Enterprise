package com.sk.workitem.app.service.helper;

import java.security.SecureRandom;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Component;

import com.sk.workitem.app.repository.CustomTokenRepository;
import com.sk.workitem.app.service.impl.TokenGenerator;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
@PropertySource("classpath:application.properties")
public class HttpHelper {
	// characters that will be used in random string
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	private CustomTokenRepository customTokenRepo;
	
	@Autowired
	public HttpHelper(CustomTokenRepository customTokenRepo) {
		this.customTokenRepo = customTokenRepo;
	}
	
	/***
	 * Stores cookie value if rememberMe is true 
	 * @param value {@link String} rememberMe value
	 * @param response {@link HttpServletResponse} to store cookie in response
	 * @param persistentToken {@link PersistentRememberMeToken}
	 * @return {@link HttpServletResponse}
	 */
	public HttpServletResponse storeCookieRememberMe(HttpServletResponse response, String contextPath
			, PersistentRememberMeToken persistentToken) {
			//PersistentRememberMeToken tokenVal = TokenGenerator.createToken(SecurityContextHolder.getContext().getAuthentication().getName());
			System.out.println("application-context path: "+contextPath);
			System.out.println("tokenVal: "+persistentToken);
			
			//store token in database
			customTokenRepo.createNewToken(persistentToken);
			Cookie cookie = new Cookie("remember-me", persistentToken.getSeries());
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            //cookie.setSecure(true);
            cookie.setPath(contextPath);
            cookie.setMaxAge(300);
            response.addCookie(cookie);
            return response;
	}
	
	/***
	 * Generate Random String to store in session
	 * @param length {@link Integer} length of string to be generated
	 * @return {@link String} Random String of specified size
	 */
	public static String generateSecureRandomString(Integer length) {
		
        StringBuilder sb = new StringBuilder(length);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(ALPHA_NUMERIC_STRING.length());
            sb.append(ALPHA_NUMERIC_STRING.charAt(index));
        }
        return sb.toString();
    }
	
	/***
	 * checks whether RememberMeCookie exists
	 * @param cookieArr cookie[] array extracted from request
	 * @return true if rememberMeCookie is present or else false
	 */
	public boolean checkRememberMeCookieExists(Cookie[] cookieArr) {
		if(Objects.nonNull(cookieArr)) {
			if(cookieArr.length > 0) {
				for(int i=0; i < cookieArr.length; i++) {
					if(cookieArr[i].getName().equals("remember-me")) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
