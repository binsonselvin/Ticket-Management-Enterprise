package com.sk.workitem.app.service;

import java.util.Map;

import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.model.MasterOrganization;

/***
 * Registration methods are defined in this interface 
 */
public interface RegisterService {
	/**
	 * Receives data from OrgRegister.html form and validates the form fields
	 * @param inputUsername {@link String} username of the user
	 * @param inputEmail {@link String} email of the user
	 * @param inputPasscode {@link String} password of the user
	 * @param inputConfPasscode {@link String} confirm-password of the user
	 * @return  {@link Map<String, String>} null if all validations are passed
	 */
	public Map<String, String> validateRegistrationData(String inputUsername, String inputEmail
			,String inputPasscode, String inputConfPasscode, String termsAccepted);
	/***
	 * assigns values received to {@link MasterOrganization} object 
	 * @param inputUsername username of the user
	 * @param inputEmail email of the user
	 * @param inputPasscode password of the user
	 * @param inputConfPasscode confirm-password of the user
	 * @return true if inserted into database or false 
	 */
	public boolean registerOrg(String inputUsername, String inputEmail, String inputPassword);
	
	/***
	 * Receives data from OrgRegister.html form and validates the form fields
	 * inserts {@link MasterOrganization} object into the database
	 * @param masterOrgObj {@link MasterOrganization}
	 * @return true if saved to database successfully or else false
	 */
	public boolean insertMasterOrganization(MasterOrganization masterOrgObj); 
	
	/***
	 * Receives data from OrgRegister.html form and assigns to {@link MasterLogin} object
	 * @param inputUsername {@link String} inputUsername username of the user
	 * @param inputEmail {@link String} inputEmail email of the user
	 * @param inputPasscode {@link String} password of the user
	 * @return true if saved to database successfully or else false
	 */
	public boolean transformToMstrLoginAndSave(String inputUsername, String inputEmail, String inputPasscode);
	
	/***
	 * inserts {@link MasterLogin} object into the database
	 * @param masterOrgObj {@link MasterOrganization}
	 * @return true if saved to database successfully or else false
	 */
	public boolean insertMasterLogin(MasterLogin masterLoginObj); 
}
