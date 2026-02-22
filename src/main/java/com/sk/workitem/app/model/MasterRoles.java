package com.sk.workitem.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "mstr_roles")
public class MasterRoles {
	@Id
	@Column(name = "role_id")
	private int roleId;
	
	@Column(name = "role_name")
	private String roleName;
	
	@Column(updatable = false, name = "created_by")
	private String createdBy;
	
	@Column(updatable = false, name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(insertable = false, name = "last_modified_by")
	private String modifiedBy;
	
	@Column(insertable = false, name = "last_modified_at")
	private LocalDateTime modifiedAt;
}
