package com.sk.workitem.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity(name="mstr_projects")
@Data
public class MasterProject extends BaseEntity {
	@Id
	@Column(name = "project_id")
	private int projectId;
	@OneToOne
	private CustomerGroup customerGroup;
	@Column(name = "customer_name")
	private int customerName;
	@Column(name = "project_name")
	private String projectName;
	@Column(name = "project_description")
	private String projectDescription;
	@Column(name = "project_type")
	private int projectType;
	@Column(name = "start_date")
	private LocalDateTime startDate;
	@Column(name = "end_date")
	private LocalDateTime endDate;
	@Column(name = "contract_type")
	private String contractType;
	@Column(name = "manager_id")
	private String managerId;
	@Column(name = "sk_branch")
	private String skBranch;
}
