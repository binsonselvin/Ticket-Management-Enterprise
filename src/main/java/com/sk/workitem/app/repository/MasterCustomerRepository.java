package com.sk.workitem.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sk.workitem.app.model.CustomerGroup;
import com.sk.workitem.app.model.MasterCustomer;

@Repository
public interface MasterCustomerRepository extends JpaRepository<MasterCustomer, Integer>{
	/***
	 * finds related {@link MasterCustomer} by given Customer Group Name object
	 * @param customerGroup {@link String}
	 * @return returns related @link {@link List} {@link MasterCustomer} by given {@link CustomerGroup} object
	 */
	public List<MasterCustomer> findByCustomerGroup_CustomerGroupName(String customerGroupName);
	
	/***
	 * find {@link MasterCustomer} object by customer name
	 * @param customerName {@link String} customer name selected by user
	 * @return {@link MasterCustomer}
	 */
	public MasterCustomer findByCustomerName(String customerName);
}
