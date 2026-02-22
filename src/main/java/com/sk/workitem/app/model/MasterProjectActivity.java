package com.sk.workitem.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class MasterProjectActivity extends BaseEntity {
	
	@Id
	@Column(name = "project_activity_id")
	private long projectActivityId;
	
	@Column(name = "customer_group_id")
	private CustomerGroup customerGroup;
	
	@Column(name = "customer_id")
	private MasterCustomer masterCustomer;
	
	@Column(name = "activity_id")
	private MasterActivities masterActivities;
	
	@Column(name = "activity_name")
	private String activityName;
}
