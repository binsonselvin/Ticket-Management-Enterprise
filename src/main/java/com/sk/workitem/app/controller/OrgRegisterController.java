package com.sk.workitem.app.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sk.workitem.app.service.LoginService;
import com.sk.workitem.app.service.RegisterService;

@Controller
@RequestMapping("/register")
public class OrgRegisterController {

	private Logger log = LogManager.getLogger(getClass());
	private RegisterService registerService;
	private LoginService loginService;
	
	@Autowired
	public OrgRegisterController(RegisterService registerService, LoginService loginService) {
		this.registerService = registerService;
		this.loginService = loginService;
	}
	
	@GetMapping(value = { "" })
	public String getOrgRegistrationPage() {
		log.info("Controller[OrgRegisterController] /register Triggered");
		return "OrgRegister";
	}
	
	@PostMapping("")
	public String registerOrg(String inputUsername, String inputEmail
			, String inputPasscode, String inputConfPasscode, Model model, String terms) {
		
		boolean isOrgRegistered = false;
		
		log.info("Controller[OrgRegisterController] /registerOrg Triggered");
		
		/**
		 * collecting MasterOrganization object in success and RegistrationError 
		 * object in validation failure
		 */	 
		Map<String, String> validationMap = registerService.validateRegistrationData(inputUsername, inputEmail, inputPasscode, inputConfPasscode, terms);
		
		//adding validation errors in map
		if(!validationMap.isEmpty()) {
			String inputUsernameLbl = "inputUsernameErr";
			String inputEmailLbl = "inputEmailErr";
			String inputPasscodeLbl = "inputPasscodeErr";
			String inputConfPasscodeLbl = "inputConfPasscodeErr";
			String termsLbl = "termsErr";
			
			model.addAttribute(inputUsernameLbl, validationMap.get(inputUsernameLbl) != null ? validationMap.get(inputUsernameLbl) : null );
			model.addAttribute(inputEmailLbl, validationMap.get(inputEmailLbl) != null ? validationMap.get(inputEmailLbl) : null );
			model.addAttribute(inputPasscodeLbl, validationMap.get(inputPasscodeLbl) != null ? validationMap.get(inputPasscodeLbl) : null );
			model.addAttribute(inputConfPasscodeLbl, validationMap.get(inputConfPasscodeLbl) != null ? validationMap.get(inputConfPasscodeLbl) : null );
			model.addAttribute(termsLbl, validationMap.get(termsLbl) != null ? validationMap.get(termsLbl) : null );
			
			model.addAttribute("inputUsernameVal", inputUsername);
			model.addAttribute("inputEmailVal", inputEmail);
			model.addAttribute("inputPasscodeVal", inputPasscode);
			model.addAttribute("inputConfPasscodeVal", inputConfPasscode);
			model.addAttribute("termsVal", terms);
			
			System.out.println("contains validation error: ");
			return "OrgRegister";
		} else {
			/**
			 * Logic to Save Registration Detail
			 * 1. Create a User
			 * 2. Create a MasterOrgination Record
			 * 3. Keep Otp_Verified to false
			 * 4. Fetch Record from email while otp page redirection if same user
			 *    tries to create a account once again redirect him to OTP page directly
			 * 5. Reduce parameter in below method
			 */
			
			//Saving to Mstr_Organizations & Mstr_Logins
			try {
				boolean orgAlreadyExist = loginService.checkOrgExist(inputEmail);
				if(orgAlreadyExist) {
					model.addAttribute("error", "Email already registered with another organization.");
					return "OrgRegister";
				} else {
					boolean orgRegistered = registerService.registerOrg(inputUsername, inputEmail, inputPasscode);	
					if(orgRegistered) {
						return "redirect:/register/verifyCode";
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			model.addAttribute("error", "Contact Administrator Unable to Register Org.");
			return "OrgRegister";
		}
	}
	
	@GetMapping("/verifyCode")
	public String getOtpVerifyPage() {
		log.info("Controller[OrgRegisterController] /verifyCode Triggered");
		return "OrgRegisterOtp";
	}
}