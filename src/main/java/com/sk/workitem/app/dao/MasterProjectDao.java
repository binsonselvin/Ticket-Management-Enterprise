package com.sk.workitem.app.dao;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sk.workitem.app.model.MasterProject;
import com.sk.workitem.app.repository.MasterProjectRepository;

@Component
public class MasterProjectDao {
	
	private MasterProjectRepository projectRepository;
	
	@Autowired
	public MasterProjectDao(MasterProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}
	
	
	/***
	 * Save {@link MasterProject} Entity to database
	 * @param projectMstr {@link MasterProject} object to save
	 * @return {@link Boolean} true if saved or else false
	 */
	public boolean saveProject(MasterProject projectMstr) {
		if(Objects.nonNull(projectRepository.save(projectMstr))){
			return true;
		}	
		return false;
	}
}
