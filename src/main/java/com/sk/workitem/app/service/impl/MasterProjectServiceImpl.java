package com.sk.workitem.app.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sk.workitem.app.model.CustomerGroup;
import com.sk.workitem.app.model.MasterActivities;
import com.sk.workitem.app.model.MasterCustomer;
import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.model.MasterProject;
import com.sk.workitem.app.model.MasterProjectType;
import com.sk.workitem.app.model.MasterRoles;
import com.sk.workitem.app.model.MasterSKBranch;
import com.sk.workitem.app.repository.CustomerGroupRepository;
import com.sk.workitem.app.repository.MasterActivitiyRepository;
import com.sk.workitem.app.repository.MasterCustomerRepository;
import com.sk.workitem.app.repository.MasterLoginRepository;
import com.sk.workitem.app.repository.MasterProjectTypeRepository;
import com.sk.workitem.app.repository.MasterRolesRepository;
import com.sk.workitem.app.repository.MasterSKBranchRepository;
import com.sk.workitem.app.service.MasterProjectService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class MasterProjectServiceImpl implements MasterProjectService {

	private CustomerGroupRepository custGroupRepo;
	private MasterProjectTypeRepository projectTypeRepo;
	private MasterSKBranchRepository skBranchRepo;
	private MasterCustomerRepository customerRepo;
	private MasterActivitiyRepository activityRepo;
	private MasterLoginRepository loginRepo;
	private MasterRolesRepository rolesRepo;
	private Logger log = LogManager.getLogger(getClass());

	@Autowired
	public MasterProjectServiceImpl(CustomerGroupRepository custGroupRepo, MasterProjectTypeRepository projectTypeRepo,
			MasterSKBranchRepository skBranchRepo, MasterCustomerRepository customerRepo,
			MasterActivitiyRepository activityRepo, MasterLoginRepository loginRepo, MasterRolesRepository rolesRepo) {
		this.custGroupRepo = custGroupRepo;
		this.projectTypeRepo = projectTypeRepo;
		this.skBranchRepo = skBranchRepo;
		this.customerRepo = customerRepo;
		this.activityRepo = activityRepo;
		this.loginRepo = loginRepo;
		this.rolesRepo = rolesRepo;
	}

	@Override
	public Map<String, Object> getProjectInitialLoadParams() {

		// Global Data Map
		Map<String, Object> dataMap = new HashMap<>();

		// Fetching CustomerGroup
		List<CustomerGroup> custGroupList = custGroupRepo.findAllByCustomerGroupNameNot("NONE");
		if (!custGroupList.isEmpty()) {
			dataMap.put("customerGroupList", custGroupList);
		}

		// Fetching MasterProjectType
		List<MasterProjectType> projectTypeList = new ArrayList<>();
		projectTypeRepo.findAll().forEach(projectTypeList::add);
		dataMap.put("projectTypeList", projectTypeList);

		// Fetch MasterBranches
		List<MasterSKBranch> skBranchList = new ArrayList<>();
		skBranchRepo.findAll().forEach(skBranchList::add);
		dataMap.put("skBranchList", skBranchList);
		
		List<MasterCustomer> masterCustomerNonGroupList = new ArrayList<MasterCustomer>();
		masterCustomerNonGroupList = customerRepo.findByCustomerGroup_CustomerGroupName("NONE");
		dataMap.put("nonGroupRelatedCustomer", masterCustomerNonGroupList);

		return dataMap;
	}

	@Override
	public List<MasterCustomer> getRelatedCustomer(String customerGroup) {
		// ArrayList for storing
		List<MasterCustomer> masterCustList = new ArrayList();

		CustomerGroup dbCustObj = custGroupRepo.findByCustomerGroupName(customerGroup);
		log.info("Customer Group {}", dbCustObj);
		if (Objects.nonNull(dbCustObj)) {
			masterCustList = customerRepo.findByCustomerGroup_CustomerGroupName(dbCustObj.getCustomerGroupName());
			return masterCustList;
		} else {
			return masterCustList;
		}
	}

	@Override
	public List<MasterActivities> getRelatedActivities(String projectType) {
		// ArrayList for storing
		List<MasterActivities> masterActivityList = new ArrayList();

		MasterProjectType dbProjectTypeObj = projectTypeRepo.findFirstByProjectTypeName(projectType);
		log.info("Project Type Object {}", dbProjectTypeObj);
		if (Objects.nonNull(dbProjectTypeObj)) {
			masterActivityList = activityRepo.findByProjectType_ProjectTypeName(projectType);
			return masterActivityList;
		} else {
			return masterActivityList;
		}
	}

	@Override
	public List<MasterLogin> getBranchRelatedManager(String branchName) {

		// Fetch ManagerRole user
		List<MasterLogin> managerList = new ArrayList();
		MasterRoles managerRole = rolesRepo.findByRoleName("MANAGER");
		// filtering out enc-password, salt, hash
		loginRepo.findAllByRoleIdAndSkBranch_BranchName(managerRole.getRoleId(), branchName).forEach(data -> {
			data.setPasswordHash(null);
			data.setPassEncrypted(null);
			data.setSalt(null);
			managerList.add(data);
		});
		
		return managerList;
	}

	@Override
	public Map<String, String> validateAndSaveProject(HttpServletRequest request, HttpServletRequest response) {
		
		//Global Error Map
		Map<String, String> errorMap = new HashMap();
		
		//customer name validation
		String customerName = request.getParameter("customerName");
		errorMap.putAll(validateCustomerName(customerName));
		
		//validation projectType 
		String projectType = request.getParameter("projectType");
		errorMap.putAll(validateProjectType(projectType));
		
		// for activityType
		String projectActivity = request.getParameter("projectActivityValue");
		System.out.println("Activity: "+projectActivity);
		errorMap.putAll(validateActivity(projectActivity));
		
		//validation for contractType
		String contractType = request.getParameter("contractType");
		if(Objects.nonNull(contractType)) {
			if(contractType.equals("")) {
				errorMap.put("contractTypeError", "Invalid contract type");
				errorMap.put("disableSubmit", "true");
			}
		} else {
			errorMap.put("contractTypeError", "Please select contract type");
			errorMap.put("disableSubmit", "true");
		}

		//validation for SKBranch
		String skBranch = request.getParameter("skBranch");
		errorMap.putAll(validateSKBranch(skBranch));
		
		//validation for projectManager
		String projectManager = request.getParameter("projectManager");
		errorMap.putAll(validateProjectManager(projectManager));
		
		//validation for startdate
		String startDate = request.getParameter("startDate");
		errorMap.putAll(validateStartDate(startDate));
		
		String endDate = request.getParameter("endDate");
		errorMap.putAll(validateEndDate(endDate, startDate));
		
		if(errorMap.size() == 0) {
			
		}
		
		return errorMap;
	}
	
	public MasterProject convertToMasterProjectObj(HttpServletRequest request) {
		
		MasterProject masterProject = new MasterProject();
		String customerGroup = request.getParameter("customerGroup");
		String customerName = request.getParameter("customerName");
		String projectType = request.getParameter("projectType");
		String projectActivity = request.getParameter("projectActivityValue");
		String contractType = request.getParameter("contractType");
		String skBranch = request.getParameter("skBranch");
		String projectManager = request.getParameter("projectManager");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		
		//necessary objects
		MasterProjectType mstrProjectType = projectTypeRepo.findFirstByProjectTypeName(projectType);
		
		if(customerGroup!=null) {
			masterProject.setCustomerGroup(custGroupRepo.findByCustomerGroupName(customerGroup));
		}
		
		masterProject.setCustomerName(customerRepo.findByCustomerName(customerName).getCustomerId());
		masterProject.setProjectType(mstrProjectType.getProjectTypeId());
		
		MasterActivities activity = new MasterActivities();
		activity.setProjectType(mstrProjectType);
		
		//activity.set
		String[] actIdArr = projectActivity.substring(0, projectActivity.length()-1) .split(",");
		List<MasterActivities> activityList = new ArrayList<MasterActivities>();
		
		for(String activityId : actIdArr) {
			activityList.add(activityRepo.findById(Integer.parseInt(activityId)).get());
		}
		//activityRepo.save(null)
		
		return null;
	}
	
	/***
	 * find CustomerGroup by customerGroupName 
	 * @param customerSelected {@link String} 
	 * @return {@link CustomerGroup}t
	 */
	public CustomerGroup checkCustomerGroup(String customerSelected) {
		return custGroupRepo.findByCustomerGroupName(customerSelected);
	}
	
	//project creation form validation methods
	/***
	 * validate customerName field received 
	 * @param customerName {@link String} customerName received from select component
	 * @return {@link Map} empty map if no errors or else map with key customerNameError
	 */
	private Map<String, String> validateCustomerName(String customerName) {
		//error map
		Map<String, String> errorMap = new HashMap();
		String customerErrLabel = "customerNameError";
		
		if(Objects.nonNull(customerName)) {
			if(customerName.isBlank()) {
				errorMap.put(customerErrLabel, "Please select customer name");
				errorMap.put("disableSubmit", "true");
			} else {
				//check in database customer_id exists
				try {
					Optional<MasterCustomer> mstrCustomerObjOpt = customerRepo.findById(Integer.parseInt(customerName));
					if(mstrCustomerObjOpt.isEmpty()) {
						errorMap.put(customerErrLabel, "Invalid customer name");
						errorMap.put("disableSubmit", "true");
						log.info("Customer record doesn't exist in database");
					}
				} catch(NumberFormatException exp) {
					errorMap.put(customerErrLabel, "Invalid customer name");
					errorMap.put("disableSubmit", "true");
					log.info("Cannot cast customerId to integer: {}", exp.getMessage());
					exp.printStackTrace();
				}
			}
		} else {
			errorMap.put("customerNameError", "Please select customer name");
			errorMap.put("disableSubmit", "true");
		}
		return errorMap;
	}
	
	/***
	 * check validation for null, empty string, data exist in database 
	 * @param projectType {@link String} projectType selected
	 * @return {@link Map} empty map if no error or else error message populated map
	 */
	private Map<String, String> validateProjectType(String projectType) {
		//error map
		Map<String, String> errorMap = new HashMap();
		String projectTypeErrLabel = "projectTypeError";
		
		if(Objects.isNull(projectType)) {
			errorMap.put(projectTypeErrLabel, "please select project/application type");
			errorMap.put("disableSubmit", "true");
		} else {
			MasterProjectType projectTypeObj = projectTypeRepo.findFirstByProjectTypeName(projectType);
			if(Objects.isNull(projectTypeObj)) {
				errorMap.put("projectTypeError", "Invalid project/application type");
				errorMap.put("disableSubmit", "true");
			}
		}
		return errorMap;
	}
	
	/***
	 * check validation for null, data exist in database 
	 * @param projectActivity
	 * @return {@link Map} empty map if no error or else error message populated map
	 */
	private Map<String, String> validateActivity(String projectActivity) {
		//error map
		Map<String, String> errorMap = new HashMap();
		if(Objects.isNull(projectActivity)) {
			errorMap.put("projectActivityError", "Please select Activity type");
			errorMap.put("disableSubmit", "true");
		} else {
			try {
				if(projectActivity.equals("")) {
					errorMap.put("projectActivityError", "Please select activity");
					errorMap.put("disableSubmit", "true");
				}else {
					boolean activityError = false;
					String projectActivitySubStr = projectActivity.substring(0, projectActivity.length()-1);
					String[] activityListArr = projectActivitySubStr.split(",");
					for(String act : activityListArr) {
						if(!activityError) {
							Optional<MasterActivities> mstrActivityObjOpt = activityRepo.findById(Integer.parseInt(act));
							if(mstrActivityObjOpt.isEmpty()) {
								errorMap.put("projectActivityError", "Activity does not exist");
								errorMap.put("disableSubmit", "true");
								activityError = true;
							}
						}
					}
				}
			} catch(NumberFormatException exp) {
				errorMap.put("projectActivityError", "Invalid Activity Type");
				errorMap.put("disableSubmit", "true");
			}
		}
		return errorMap;
	}
	
	/***
	 * validate SKBranch for null and value exist in database
	 * @param skBranch {@link String}
	 * @return {@link Map} empty map if no error or else error message populated map
	 */
	private Map<String, String> validateSKBranch(String skBranch) {
		//error map
		Map<String, String> errorMap = new HashMap();
		
		if(Objects.isNull(skBranch)) {
			errorMap.put("skBranchError", "Please select sk branch");
			errorMap.put("disableSubmit", "true");
		} else {
			Optional<MasterSKBranch> skBranchOpt = skBranchRepo.findByBranchName(skBranch);
			if (skBranchOpt.isEmpty()) {
				errorMap.put("skBranchError", "SK Branch does not exist");
				errorMap.put("disableSubmit", "true");
			}
		}
		
		return errorMap;
	}
	
	/***
	 * validate projectManager for null and value exist in database
	 * @param projectManager
	 * @return {@link Map} empty map if no error or else error message populated map
	 */
	private Map<String, String> validateProjectManager(String projectManager) {
		//error map
		Map<String, String> errorMap = new HashMap();
		
		if(Objects.isNull(projectManager)) {
			errorMap.put("projectManagerError", "Please select manager");
			errorMap.put("disableSubmit", "true");
		} else {
			Optional<MasterLogin> projectManagerOpt = loginRepo.findById(projectManager) ;
			if (projectManagerOpt.isEmpty()) {
				errorMap.put("projectManagerError", "Manager does not exist");
				errorMap.put("disableSubmit", "true");
			}
		}
		return errorMap;
	}
	
	/***
	 * validate startDate for null, date format check, date cannot be in past
	 * @param projectManager
	 * @return {@link Map} empty map if no error or else error message populated map
	 */
	private Map<String, String> validateStartDate(String startDate) {
		//error map
		Map<String, String> errorMap = new HashMap();
		LocalDate startDateConverted = null;
		
		// null check
		// date format check
		// start date cannot be before today
		if (Objects.isNull(startDate)) {
			errorMap.put("startDateError", "Start Date cannot be empty");
			errorMap.put("disableSubmit", "true");
		} else {
			try {
				startDateConverted = LocalDate.parse(startDate);
				if (startDateConverted.isBefore(LocalDate.now())) {
					errorMap.put("startDateError", "Start date cannot be in past");
					errorMap.put("disableSubmit", "true");
				}
			} catch (DateTimeParseException exp) {
				log.info("[Exception] Unable to parse startDate: {}", exp.getMessage());
				exp.printStackTrace();
				errorMap.put("startDateError", "Invalid start date");
				errorMap.put("disableSubmit", "true");
			}
		}
		return errorMap;
	}
	
	/***
	 * validate endDate for null, date format check, end date cannot before start
	 * @param endDate {@link LocalDate} project endDate selected by user 
	 * @param startDate {@link LocalDate} project startDate selected by user
	 * @return {@link Map} empty map if no error or else error message populated map
	 */
	private Map<String, String> validateEndDate(String endDate, String startDate) {
		//error map
		Map<String, String> errorMap = new HashMap();
		String errorDateLabel = "endDateError";
		LocalDate endDateConverted = null;
		LocalDate startDateConverted = null;
		
		//null check
		//date format check
		//end date cannot be before start date
		if(Objects.isNull(endDate)) {
			errorMap.put(errorDateLabel, "End date cannot be empty");
			errorMap.put("disableSubmit", "true");
		} else {
			try {
				startDateConverted = LocalDate.parse(startDate);
				endDateConverted = LocalDate.parse(endDate);
				if(endDateConverted.isBefore(startDateConverted)) {
					errorMap.put(errorDateLabel, "End date cannot be before start date");
					errorMap.put("disableSubmit", "true");
				}
			} catch(DateTimeParseException exp) {
				log.info("[Exception] Unable to parse endDate: {}",exp.getMessage());
				exp.printStackTrace();
				errorMap.put(errorDateLabel, "Invalid end date");
				errorMap.put("disableSubmit", "true");
			}
		}
		return errorMap;
	}

	@Override
	public RedirectAttributes populateProjectErrorModel(RedirectAttributes model, Map<String, String> errorMap) {
		if(errorMap.isEmpty()) {
			return model;
		} else {
			// for disabling submit button
			if(errorMap.containsKey("disableSubmit")) {
				model.addFlashAttribute("disableSubmit", errorMap.get("disableSubmit"));
			}
			
			//for endDate error
			if(errorMap.containsKey("endDateError")) {
				model.addFlashAttribute("endDateError", errorMap.get("endDateError"));
			}
			
			//for startDate error
			if(errorMap.containsKey("startDateError")) {
				model.addFlashAttribute("startDateError", errorMap.get("startDateError"));
			}
			
			//for activity error
			if(errorMap.containsKey("projectActivityError")) {
				model.addFlashAttribute("projectActivityError", errorMap.get("projectActivityError"));
			}
			
			//for branch error
			if(errorMap.containsKey("skBranchError")) {
				model.addFlashAttribute("skBranchError", errorMap.get("skBranchError"));
			}
			
			//for customer error
			if(errorMap.containsKey("customerNameError")) {
				model.addFlashAttribute("customerNameError", errorMap.get("customerNameError"));
			}
			
			//for project type error
			if(errorMap.containsKey("projectTypeError")) {
				model.addFlashAttribute("projectTypeError", errorMap.get("projectTypeError"));
			}
			
			//for contract type error
			if(errorMap.containsKey("contractTypeError")) {
				model.addFlashAttribute("contractTypeError", errorMap.get("contractTypeError"));
			}
			
			//for contract type error
			if(errorMap.containsKey("projectManagerError")) {
				model.addFlashAttribute("projectManagerError", errorMap.get("projectManagerError"));
			}
		}
		
		return model;
	}
}