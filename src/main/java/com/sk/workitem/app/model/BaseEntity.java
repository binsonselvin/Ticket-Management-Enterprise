package com.sk.workitem.app.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import lombok.Data;

@Data
@EntityListeners(value = AuditingEntityListener.class)
public class BaseEntity {
	
	@CreatedBy
	@Column(updatable = false, name = "created_by")
	private String createdBy;
	
	@CreatedDate
	@Column(updatable = false, name = "created_at")
	private LocalDateTime createdAt;
	
	@LastModifiedBy
	@Column(insertable = false, name = "last_modified_by")
	private String modifiedBy;
	
	@LastModifiedDate
	@Column(insertable = false, name = "last_modified_at")
	private LocalDateTime modifiedAt;
}
