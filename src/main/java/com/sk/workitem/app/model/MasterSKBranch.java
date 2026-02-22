package com.sk.workitem.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name="mstr_branches")
public class MasterSKBranch extends BaseEntity{
	
	@Id
	@Column(name = "branch_id")
	private int branchId;
	
	@Column(name = "branch_name")
	private String branchName;
	
	@Column(name = "branch_city")
	private String branchCity;
	
	@Column(name = "branch_address")
	private String branchAddress;
	
	@Column(name = "branch_contact")
	private String branchContact;
}
