package com.sk.workitem.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sk.workitem.app.model.MasterProjectType;

@Repository
public interface MasterProjectTypeRepository extends CrudRepository<MasterProjectType, Integer> {
	/***
	 * find a single record of {@link MasterProjectType} using projectTypeName
	 * @param projectTypeName
	 * @return {@link MasterProjectType}
	 */
	public MasterProjectType findFirstByProjectTypeName(String projectTypeName);
}
