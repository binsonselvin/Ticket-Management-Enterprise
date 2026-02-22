package com.sk.workitem.app.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sk.workitem.app.constants.SystemUserConstant;
import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.model.MasterOrganization;
import com.sk.workitem.app.model.MasterRoles;
import com.sk.workitem.app.repository.MasterLoginRepository;
import com.sk.workitem.app.repository.MasterOrganizationRepository;
import com.sk.workitem.app.repository.MasterRolesRepository;
import com.sk.workitem.app.service.LoginService;
import com.sk.workitem.app.service.RegisterService;
import com.sk.workitem.app.service.helper.RegistrationHelper;

import jakarta.transaction.Transactional;

@Component
public class RegisterServiceImpl implements RegisterService {

	private Logger log = LogManager.getLogger(getClass());
	
	//service objects
	private LoginService loginService;
	
	//private repository objects
	private MasterOrganizationRepository masterOrgRepo;
	private MasterLoginRepository masterLoginRepo;
	private MasterRolesRepository masterRolesRepo;
	
	@Autowired
	public RegisterServiceImpl(MasterOrganizationRepository masterOrgRepo, LoginService loginService, 
			MasterLoginRepository masterLoginRepo, MasterRolesRepository masterRolesRepo) {
		this.masterOrgRepo = masterOrgRepo;
		this.loginService = loginService;
		this.masterLoginRepo = masterLoginRepo;
		this.masterRolesRepo = masterRolesRepo;
	}
	
	/**
	 * Receives data from OrgRegister.html form and assigns it to {@link MasterOrganization} object
	 * @param inputUsername {@link String} username of the user
	 * @param inputEmail {@link String} email of the user
	 * @param inputPasscode {@link String} password of the user
	 * @param inputConfPasscode {@link String} confirm-password of the user
	 * @return  {@link Map<String, Object>} the object may be an error object or {@link MasterOrganization}
	 */
	@Override
	public Map<String, String> validateRegistrationData(String inputUsername, String inputEmail,
			String inputPasscode, String inputConfPasscode, String termsAccepted) {

		// store overall validation of fields
		Map<String, String> resultMap = new HashMap<>();
		
		//performing validation on inputUsername field
		Map<String, String> usernameValidationMap = RegistrationHelper.validateUsername(inputUsername.trim());
		if(!usernameValidationMap.isEmpty()) {
			resultMap.putAll(usernameValidationMap);
		}
		
		//performing validation on inputEmail field
		Map<String, String> workEmailValidationMap = RegistrationHelper.validateEmail(inputEmail.trim());
		if(!workEmailValidationMap.isEmpty()) {
			resultMap.putAll(workEmailValidationMap);
		}
		
		//performing validation on password field
		Map<String, String> passwordValidationMap = RegistrationHelper.validatePassword(inputPasscode.trim());
		if(!passwordValidationMap.isEmpty()) {
			resultMap.putAll(passwordValidationMap);
		}
		
		if(inputConfPasscode!=null) {
			Map<String, String> confirmPasscodeValidationMap = new HashMap<>();
			if(inputConfPasscode.equals("")) {
				confirmPasscodeValidationMap.put("inputConfPasscodeErr", "Confirm Password cannot be blank");
				resultMap.putAll(confirmPasscodeValidationMap);
			} else if(!inputConfPasscode.equals(inputPasscode)) {
				confirmPasscodeValidationMap.put("inputConfPasscodeErr", "Password and Confirm Password not matching");
				resultMap.putAll(confirmPasscodeValidationMap);
			}
		}
		
		if(termsAccepted == null) {
			termsAccepted = "";
		}
		
		if(!termsAccepted.equals("checked")) {
			resultMap.put("termsErr", "read and accept the terms and conditions");
		}
		
		return resultMap;
	}

	@Override
	@Transactional(rollbackOn = { Exception.class })
	public boolean registerOrg(String inputUsername, String inputEmail, String password) {
		
		try {
			MasterOrganization masterOrgObj = new MasterOrganization();
			masterOrgObj.setOrgName("SK INTERNATIONAL");
			masterOrgObj.setUsername(inputUsername);
			masterOrgObj.setUserEmail(inputEmail);
			masterOrgObj.setOrgVerified(false);
			masterOrgObj.setCreatedBy(SystemUserConstant.SYSTEM);
			masterOrgObj.setCreatedAt(LocalDateTime.now());
		
			//inserting into database
			return insertMasterOrganization(masterOrgObj) && transformToMstrLoginAndSave(inputUsername, inputEmail, password);
		} catch(Exception e) {
			return false;
		}
	}

	@Override
	public boolean insertMasterOrganization(MasterOrganization masterOrgObj) {
		try {
			masterOrgRepo.save(masterOrgObj);
			return true;
		} catch(Exception e) {
			log.error("Cannot Save MasterOrganization Object: {}",e.getMessage());
			return false;
		}
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean transformToMstrLoginAndSave(String inputUsername, String inputEmail, String inputPasscode) {
		
		MasterOrganization masterOrg = masterOrgRepo.findByUserEmail(inputEmail);
		
		byte[] encryptedPwd = loginService.encryptPasswordPublicKey(inputPasscode);
		String salt = loginService.genPasswordSalt();
		String hash = loginService.hashPassword(inputPasscode, salt);
		
		int roleId = masterRolesRepo.findByRoleName("PORTAL_OWNER").getRoleId();
		
		MasterLogin masterLogin = new MasterLogin();
		masterLogin.setUsername(inputUsername);
		masterLogin.setUserEmail(inputEmail);
		masterLogin.setOrgId(masterOrg.getOrgId());
		masterLogin.setRoleId(roleId);
		masterLogin.setPasswordHash(hash);
		masterLogin.setPassEncrypted(encryptedPwd);
		masterLogin.setSalt(salt);
		masterLogin.setFailedCount(0);
		masterLogin.setAccLocked(false);
		masterLogin.setCreatedBy(SystemUserConstant.SYSTEM);
		masterLogin.setCreatedAt(LocalDateTime.now());
		
		return insertMasterLogin(masterLogin);
	}

	@Override
	public boolean insertMasterLogin(MasterLogin masterLoginObj) {
		try {
			masterLoginRepo.save(masterLoginObj);
			return true;
		} catch (Exception e) {
			log.error("ERROR Couldn't save MasterLogin data : ",e);
			return false;
		}
	}

}
