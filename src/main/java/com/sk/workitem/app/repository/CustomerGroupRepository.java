package com.sk.workitem.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sk.workitem.app.model.CustomerGroup;

@Repository
public interface CustomerGroupRepository extends JpaRepository<CustomerGroup, Integer>{
	
	public CustomerGroup findByCustomerGroupName(String CustomerGroup);
	
	public List<CustomerGroup> findAllByCustomerGroupNameNot(String CustomerGroup);
}
