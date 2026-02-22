package com.sk.workitem.app.service;

import org.springframework.stereotype.Service;

import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.model.MasterOtp;

import jakarta.servlet.http.HttpSession;

@Service
public interface LoginService {
	/***
	 * Encrypt the given data   
	 * @param password {@link String} data
	 * @return byte[] encrypted data
	 */
	public byte[] encryptPasswordPublicKey(String password);
	
	/***
	 * Decrypt the encrypted data   
	 * @param password {@link String} data
	 * @return {@link String} encrypted data
	 */
	public byte[] decryptPasswordPrivateKey(byte[] password);
	
	/***
	 * Encrypts the password using BCrypt library
	 * @param password user entered password
	 * @return {@link String} encrypted hash
	 */
	public String hashPassword(String password, String salt);
	
	/***
	 * Generates a salt value
	 * @return {@link String} salt 
	 */
	public String genPasswordSalt();
	
	/***
	 * Check whether email and orgName are registered before
	 * @param email {@link String} user email
	 * @return true if the email is already associated with a organization or else false
	 */
	public boolean checkOrgExist(String email);
	
	/***
	 * Checks whether the user entered email exists in the database
	 * @param email {@link String} email address of user
	 * @return {@link MasterLogin} if found or else false
	 */
	public MasterLogin checkUserExists(String email);
	
	/***
	 * Validates whether the user entered password is correct or not
	 * @param formPwd {@link String} user entered password from the front end form
	 * @param dbHash {@link String} hash stored in the database
	 * @return true if password is correct or else false
	 */
	public boolean validateUserPwd(String formPwd, String dbHash);
	
	/***
	 * Generates a 6 digit security code 
	 * @return {@link String} security code
	 */
	public String generateSecurityCode();
	
	/***
	 * updates successful login timestamp for the user 
	 * @param masterLogin {@link MasterLogin} user object
	 */
	public void updateLoginTimestamps(MasterLogin masterLogin);
	
	/***
	 * update unsuccessful login timestamp for the user
	 * @param masterLogin {@link MasterLogin} user object
	 */
	public void updateFailureLoginTimestamps(MasterLogin masterLogin);
	
	/***
	 * Save new account password to the database
	 * @param email {@link String} email of the user
	 * @param password {@link String} password of the user
	 * @return true if password is persisted to database or else false 
	 */ 
	public boolean updateAccountPassword(String email, String password);
	
	/***
	 * get the last login otp generated from the application for a particular user 
	 * @param otpGenFor {@link String} otp request purpose
	 * @param email {@link String} email of the user
	 * @return {@link MasterOtp} object of MasterOtp
	 */
	public MasterOtp getLastLoginOtp(String otpGenFor, String email);
	
	/***
	 * check whether the user has exceeded the resend count
	 * @param email {@link String} email of the user 
	 * @return true if resend count limit is reached or else false
	 */
	public boolean checkResendCountExceeded(String email);
	
	/***
	 * increase resend count of the user
	 * @param email {@link String}
	 * @return {@link Boolean} true if increased or else false
	 */
	public boolean increaseResendCount(String email);
	
}
