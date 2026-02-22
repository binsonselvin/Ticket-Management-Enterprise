package com.sk.workitem.app.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity(name = "mstr_activities")
public class MasterActivities extends BaseEntity {
	@Id
	@Column(name="activity_id")
	private int activityId;
	
	@Column(name="activity_name")
	private String activityName;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "project_type", referencedColumnName = "project_type_id")
	private MasterProjectType projectType;
	
	@Column(name="is_custom")
	private boolean isCustom;
}
