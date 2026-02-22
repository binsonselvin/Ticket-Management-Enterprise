package com.sk.workitem.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sk.workitem.app.model.MasterOrganization;

@Repository
public interface MasterOrganizationRepository extends CrudRepository<MasterOrganization, Integer>{
	public MasterOrganization findByUserEmail(String userEmail);
}
