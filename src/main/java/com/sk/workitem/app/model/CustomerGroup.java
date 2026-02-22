package com.sk.workitem.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Data
@Entity(name = "mstr_customer_group")
public class CustomerGroup {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mstr_customer_group_customer_group_id_seq")
	@SequenceGenerator(name = "mstr_customer_group_customer_group_id_seq", allocationSize = 1, initialValue = 1, sequenceName = "mstr_customer_group_customer_group_id_seq")
	@Column(name = "customer_group_id")
	private int customerGroupId;
	
	@Column(name = "customer_group")
	private String customerGroupName;
	
	@Column(updatable = false, name = "created_by")
	private String createdBy;
	
	@Column(updatable = false, name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(insertable = false, name = "modified_by")
	private String modifiedBy;
	
	@Column(insertable = false, name = "modified_at")
	private LocalDateTime modifiedAt;
}
