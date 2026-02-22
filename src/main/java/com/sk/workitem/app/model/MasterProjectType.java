package com.sk.workitem.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Data
@Entity(name="mstr_project_types")
public class MasterProjectType extends BaseEntity{
	
	@Id
	@Column(name="project_type_id")
	private int projectTypeId;
	
	@Column(name="project_type_name")
	private String projectTypeName;
}
