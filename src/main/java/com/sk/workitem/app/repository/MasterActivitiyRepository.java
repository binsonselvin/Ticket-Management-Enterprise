package com.sk.workitem.app.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sk.workitem.app.model.MasterActivities;

@Repository
public interface MasterActivitiyRepository extends CrudRepository<MasterActivities, Integer> {
	
	/***
	 * find list of MasterActivities linked to selected projectType from database
	 * @param projectTypeName {@link String} user selected projectType
	 * @return return {@link List} of MasterActivities linked to selected projectType
	 */
	public List<MasterActivities> findByProjectType_ProjectTypeName(String projectTypeName);
}
