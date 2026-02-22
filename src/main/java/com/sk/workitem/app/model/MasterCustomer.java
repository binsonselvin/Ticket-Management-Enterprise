package com.sk.workitem.app.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity(name = "mstr_customers")
public class MasterCustomer extends BaseEntity {
	@Id
	private int customerId;
	
	@Column(name = "customer_name")
	private String customerName;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="customer_group", referencedColumnName = "customer_group_id", nullable = true)
	private CustomerGroup customerGroup;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "customer_address")
	private String customerAddress;
	
	@Column(name = "customer_city")
	private String customerCity;
	
	@Column(name = "handling_sk_branch")
	private int handlingSkBranch;
}
