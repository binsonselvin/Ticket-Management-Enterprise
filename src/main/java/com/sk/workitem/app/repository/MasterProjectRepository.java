package com.sk.workitem.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sk.workitem.app.model.MasterProject;

@Repository
public interface MasterProjectRepository extends CrudRepository<MasterProject, Integer> {

}
