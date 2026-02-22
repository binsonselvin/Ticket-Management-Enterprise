package com.sk.workitem.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Data
@Entity(name = "mstr_organizations")
public class MasterOrganization {
	@Id
	@Column(name = "org_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mstr_organization_org_id_seq")
	@SequenceGenerator(name = "mstr_organization_org_id_seq", allocationSize = 1, initialValue = 1, sequenceName = "mstr_organization_org_id_seq")
	private int orgId;
	
	@Column(name = "org_name")
	private String orgName;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "user_email")
	private String userEmail;
	
	@Column(name = "org_verified")
	private boolean orgVerified;
	
	@Column(updatable = false, name = "created_by")
	private String createdBy;
	
	@Column(updatable = false, name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(insertable = false, name = "last_modified_by")
	private String modifiedBy;
	
	@Column(insertable = false, name = "last_modified_at")
	private LocalDateTime modifiedAt;
}
