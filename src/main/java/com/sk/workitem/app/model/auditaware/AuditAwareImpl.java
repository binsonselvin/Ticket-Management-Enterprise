package com.sk.workitem.app.model.auditaware;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditAwareImpl implements AuditorAware<String>{

	/***
	 * return email of the user for updated_by and created_by field  
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.ofNullable(SecurityContextHolder.getContext()
				.getAuthentication()
				.getName());
	}
	
}