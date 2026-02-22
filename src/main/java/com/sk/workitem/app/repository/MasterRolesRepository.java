package com.sk.workitem.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sk.workitem.app.model.MasterRoles;

@Repository
public interface MasterRolesRepository extends CrudRepository<MasterRoles, Integer>{
	
	public MasterRoles findByRoleName(String roleName);
}
