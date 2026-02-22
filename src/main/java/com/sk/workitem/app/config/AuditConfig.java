package com.sk.workitem.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import com.sk.workitem.app.model.auditaware.AuditAwareImpl;

@Configuration
public class AuditConfig {
	
	/**
	 * 
	 * @return {@link AuditorAware} mostly email of the auditor
	 */
	@Bean(name = "auditAwareImpl")
	public AuditorAware<String> auditorProvider() {
		return new AuditAwareImpl();
	}
	
}