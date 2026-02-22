package com.sk.workitem.app.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sk.workitem.app.model.MasterLogin;

@Repository
public interface MasterLoginRepository extends CrudRepository<MasterLogin, String>{
	
	/***
	 * Get list of MasterLogin based on passed roleId
	 * @param roleId (int) manager role id  
	 * @return {@link List} MasterLogin based on passed roleId
	 */
	List<MasterLogin> findAllByRoleId(int roleId);
	
	/***
	 * Get list of MasterLogin based on passed role and branch name
	 * @param roleId (int) 
	 * @param branchName {@link String} SK branch name selected by user
	 * @return {@link List} MasterLogin based on passed role and branch name
	 */
	List<MasterLogin> findAllByRoleIdAndSkBranch_BranchName(int roleId, String branchName);
}
