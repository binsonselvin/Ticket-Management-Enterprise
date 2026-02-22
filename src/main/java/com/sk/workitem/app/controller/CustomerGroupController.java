package com.sk.workitem.app.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sk.workitem.app.service.CustomerGroupService;
import com.sk.workitem.app.service.LoginService;
import com.sk.workitem.app.service.RegisterService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/customergroup")
public class CustomerGroupController {
	
	private CustomerGroupService customerGroupService;
	
	private Logger log = LogManager.getLogger(getClass());
	
	
	@Autowired
	public CustomerGroupController(CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
		
	}
	
	// Viewing Customer Group Page
	
	@GetMapping(path =  {"/", ""})
	public String getCustomeGroupPage()
	{
		return "admin-customer-group";
	}
	
	// Save Customer Group in a Database
	
	@PostMapping("/saveGroup")
	public String saveCustomerGroup(@RequestParam("customergroup") String customerGroup ,Model model)
	{
		log.info("Controller[CustomerGroupController] /testing");
		
		String Validerror=customerGroupService.validateCustomerGorup(customerGroup);
		System.out.println("valid"+Validerror);
		if(Validerror !=null)
		{
		     model.addAttribute("Validerror", Validerror);
		     return "admin-customer-group";
		}
		else
		{
		Boolean customerGroupAlreadyExist=	customerGroupService.checkCustomerGroupExist(customerGroup);
			if(customerGroupAlreadyExist)
			{
				model.addAttribute("error", "Customer Group Already Exist .");
				  return "admin-customer-group";
			}
			else
			{	
			String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		     Boolean saveCustomerGroup=	customerGroupService.saveCustomeGroup(customerGroup,userEmail);
		 
		 if(saveCustomerGroup)
		 {
			    model.addAttribute("success", "Customer Group Successfully Inserted");
			    return "admin-customer-group";
		 }
			
		 else
		 {
			    model.addAttribute("error", "Unable to save Customer Group ");
			    return "admin-customer-group";
		 }
		 
		}
		}
		//return "admin-customer-group";
		
	}

}
