package com.sk.workitem.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sk.workitem.app.model.MasterActivities;
import com.sk.workitem.app.model.MasterCustomer;
import com.sk.workitem.app.model.MasterLogin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MasterProjectService {
	
	/***
	 * Load the customerGroup, projectType, skBranch, Manager picklist values
	 * @return {@link Map} empty map if no project groups are available or else return map with above mentioned params
	 */
	public Map<String, Object> getProjectInitialLoadParams();
	
	/***
	 * find list of MasterCustomer by given by customer group 
	 * @param customerGroup {@link String} user selected customer group
	 * @return {@link List} of {@link MasterCustomer} matching given customer group 
	 */
	public List<MasterCustomer> getRelatedCustomer(String customerGroup);
	
	/***
	 * find list of MasterActivities linked to selected projectType
	 * @param projectType {@link String} user selected projectType
	 * @return return {@link List} of MasterActivities linked to selected projectType
	 */
	public List<MasterActivities> getRelatedActivities(String projectType);
	
	/***
	 * find list of managers associated with the selected branch
	 * @param branchName {@link String} selected branch
	 * @return {@link List} MasterLogin list of managers associated with the selected branch
	 */
	public List<MasterLogin> getBranchRelatedManager(String branchName);
	
	/***
	 * validates the form data and save to database if no errors are found
	 * @param request {@link HttpServletRequest} servlet request to fetch form data
	 * @param response {@link HttpServletResponse} servlet response to add any response headers
	 * @return {@link Map} empty map if no error is found and data is saved or else map is populated with error label and error message
	 */
	public Map<String, String> validateAndSaveProject(HttpServletRequest request, HttpServletRequest response);
	
	/***
	 * populate model object with error from errorMap
	 * @param model {@link Model} model to populate
	 * @return {@link Model} model populated with error
	 */
	public RedirectAttributes populateProjectErrorModel(RedirectAttributes model, Map<String, String> errorMap); 
}
