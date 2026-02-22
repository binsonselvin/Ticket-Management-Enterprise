package com.sk.workitem.app.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sk.workitem.app.model.MasterSKBranch;

@Repository
public interface MasterSKBranchRepository extends CrudRepository<MasterSKBranch, Integer>{
	
	public Optional<MasterSKBranch> findByBranchName(String branchName);
	
}
