package com.sk.workitem.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sk.workitem.app.model.MasterActivities;
import com.sk.workitem.app.model.MasterCustomer;
import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.model.RestResponse;
import com.sk.workitem.app.service.LoginService;
import com.sk.workitem.app.service.MasterProjectService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/***
 * @author Binson Selvin
 * @since 20/09/2024
 * Controller for handling all project related activity
 */

@Controller
@RequestMapping("/admin/project")
@SessionAttributes("errorMap")
public class ProjectController {
	
	//logger initialization
	Logger log = LogManager.getLogger(getClass());
	
	private MasterProjectService masterProjectService;
	private LoginService loginService;
	
	@Autowired
	public ProjectController(MasterProjectService masterProjectService, LoginService loginService) {
		this.masterProjectService = masterProjectService;
		this.loginService = loginService;
	}
	
	@GetMapping(value = {"/"})
	public String getProjectPage(HttpServletRequest request, HttpServletResponse response) {
		log.info("Controller[ProjectController][GET] /admin/project/ Triggered");
		return "redirect:/admin/project";
	}
	
	@GetMapping("")
	public String loadProjectPage(RedirectAttributes redirectModel, Model model) {
		log.info("Controller[ProjectController][GET] /admin/project Triggered");
		//Get all data for particular user
		
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		
		if(userEmail == null) {
			redirectModel.addAttribute("error", "Please login");
			return "redirect:/login";
		}
		
		System.out.println("redirectModel errorMap: "+redirectModel.getAttribute("errorMap"));
		System.out.println("model errorMap: "+model.getAttribute("errorMap"));
		System.out.println("customerName error: "+model.getAttribute("customerNameError"));
		
		//load user details (username & email)
		MasterLogin userObj = loginService.checkUserExists(userEmail);
		model.addAttribute("username", userObj.getUsername());
		model.addAttribute("email", userObj.getUserEmail());
		
		//Load Data
		Map<String, Object> initalLoadObj = masterProjectService.getProjectInitialLoadParams();
		log.info("dataMap customerGroupList: {}",initalLoadObj.get("customerGroupList"));
		log.info("dataMap projectTypeList: {}",initalLoadObj.get("projectTypeList"));
		log.info("dataMap skBranchList: {}",initalLoadObj.get("skBranchList"));
		log.info("dataMap nonGroupRelatedCustomer: {}",initalLoadObj.get("nonGroupRelatedCustomer"));
		
		model.addAttribute("customerGroupList", initalLoadObj.get("customerGroupList"));
		model.addAttribute("projectTypeList", initalLoadObj.get("projectTypeList"));
		model.addAttribute("skBranchList", initalLoadObj.get("skBranchList"));
		model.addAttribute("nonGroupRelatedCustomer", initalLoadObj.get("nonGroupRelatedCustomer"));
		
		return "admin-project-creation";
	}
	
	@PostMapping("/save")
	public String saveProject(HttpServletRequest request, HttpServletResponse response, Model model, 
			RedirectAttributes redirectModel) {
		
		//Redirect User to Login
		if(SecurityContextHolder.getContext().getAuthentication().getName() == null) {
			return "redirect:/login";
		} 
		
		//for collecting validation errors
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap = masterProjectService.validateAndSaveProject(request, request);
		
		System.out.println("errorMap length: "+errorMap.size());
		//for populating error in model object
		redirectModel = masterProjectService.populateProjectErrorModel(redirectModel, errorMap);
		
		if(!errorMap.isEmpty()) {
			//System.out.println("error map is not empty: "+errorMap);
			redirectModel.addFlashAttribute("errorMap", errorMap);
			return "redirect:/admin/project";
		} 
		
		log.info("errorMap for project creation {}",errorMap);
		return "redirect:/admin/project";
	}
	
	@GetMapping("/relatedCustomer")
	@ResponseBody
	public ResponseEntity<RestResponse> getRelatedCustomerREST(@RequestParam String customerGroup) {
		//response obj
		RestResponse response = new RestResponse();
		//map to store list of MasterCustomer
		Map<String, Object> dataMap = new HashMap<>();
		
		log.info("Controller[ProjectController][REST][GET] /admin/project/relatedCustomer Triggered");
		log.info("CustomerGroup Selected: {}",customerGroup);
		List<MasterCustomer> mstrCustomerList = masterProjectService.getRelatedCustomer(customerGroup);
		log.info("MasterCustomerList: {}",mstrCustomerList);
		if(!mstrCustomerList.isEmpty()) {
			dataMap.put("dataMap", mstrCustomerList);
			response.setObjData(dataMap);
			response.setResponseCode("200");
			response.setResponseMsg("success");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			dataMap.put("dataMap", null);
			response.setObjData(dataMap);
			response.setResponseCode("404");
			response.setResponseMsg("error");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/relatedActivity")
	@ResponseBody
	public ResponseEntity<RestResponse> getRelatedActivity(@RequestParam String projectType) {
		//response obj
		RestResponse response = new RestResponse();
		//map to store list of MasterActivityList
		Map<String, Object> dataMap = new HashMap<>();
		
		log.info("Controller[ProjectController][REST][GET] /admin/project/relatedActivity Triggered");
		log.info("ProjectType Selected: {}", projectType);
		List<MasterActivities> mstrActivityList = masterProjectService.getRelatedActivities(projectType);
		log.info("MasterActivityList: {}",mstrActivityList);
		if(!mstrActivityList.isEmpty()) {
			dataMap.put("dataMap", mstrActivityList);
			response.setObjData(dataMap);
			response.setResponseCode("200");
			response.setResponseMsg("success");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			dataMap.put("dataMap", null);
			response.setObjData(dataMap);
			response.setResponseCode("404");
			response.setResponseMsg("error");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/relatedManager")
	@ResponseBody
	public ResponseEntity<RestResponse> getRelatedManager(@RequestParam String branchSelected) {
		//response obj
		RestResponse response = new RestResponse();
		//map to store list of MasterActivityList
		Map<String, Object> dataMap = new HashMap<>();
		
		log.info("Controller[ProjectController][REST][GET] /admin/project/relatedManager Triggered");
		log.info("Branch Selected: {}", branchSelected);
		List<MasterLogin> mstrActivityList = masterProjectService.getBranchRelatedManager(branchSelected);
		log.info("MasterActivityList: {}",mstrActivityList);
		if(!mstrActivityList.isEmpty()) {
			dataMap.put("dataMap", mstrActivityList);
			response.setObjData(dataMap);
			response.setResponseCode("200");
			response.setResponseMsg("success");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			dataMap.put("dataMap", null);
			response.setObjData(dataMap);
			response.setResponseCode("404");
			response.setResponseMsg("error");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
	}
}