package com.sk.workitem.app.config.security;

import java.io.IOException;

import javax.naming.AuthenticationException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.sk.workitem.app.errors.AccountLockedException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
		String redirectUrl = "/login?error";
        if (exception instanceof AccountLockedException) {
            redirectUrl = "/login?error=true"; // Custom page for account locked
        }
        response.sendRedirect(redirectUrl);
		
	}
}
