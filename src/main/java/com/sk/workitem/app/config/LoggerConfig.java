package com.sk.workitem.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class LoggerConfig {
	
	public static final String ADMIN_EMAIL = "binson.selvin@skinternational.com";
	
	static {
		System.setProperty("mail.smtp.starttls.enable", "true"); 
	}
	
	@Bean
	public LoggerConfig initializeLogger() {
		return new LoggerConfig();
	}
}
