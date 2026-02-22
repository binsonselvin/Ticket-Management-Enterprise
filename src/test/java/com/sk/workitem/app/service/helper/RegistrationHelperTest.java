/**
 * 
 */
package com.sk.workitem.app.service.helper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * 
 */
class RegistrationHelperTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * Test method for {@link com.sk.workitem.app.service.helper.RegistrationHelper#validateUsername(java.lang.String)}.
	 */
	@Test
	void testValidateUsername() {
		//valid username check
		Map<String, String> resultMap = RegistrationHelper.validateUsername("Binson Selvin");
		assertEquals(null, resultMap.get("inputUsernameErr"), "Valid username should return null value as there is no error");
		
		//blank username check
		resultMap = RegistrationHelper.validateUsername("");
		assertEquals("Username cannot be blank", resultMap.get("inputUsernameErr"), "Blank value should return error as **Username cannot be blank**");
		
		//username starting with special character
		resultMap = RegistrationHelper.validateUsername("$inson Selvin");
		assertEquals("Username cannot start with special characters", resultMap.get("inputUsernameErr"), "Username starting with special character should return error as **Username cannot start with special characters**");
		
		//username ending with special character
		resultMap = RegistrationHelper.validateUsername("Binson Selvin$");
		assertEquals("Username cannot end with special characters", resultMap.get("inputUsernameErr"), "Username starting with special character should return error as **Username cannot end with special characters**");
		
		//username ending with special character
		resultMap = RegistrationHelper.validateUsername("Binson Selvin$");
		assertEquals("Username cannot end with special characters", resultMap.get("inputUsernameErr"), "Username ending with special character should return error as **Username cannot end with special characters**");
		
		//username minimum length validation
		resultMap = RegistrationHelper.validateUsername("B");
		assertEquals("Username must be greater than 1 characters", resultMap.get("inputUsernameErr"), "Username length less than 2 charcter should return error as **Username must be greater than 1 characters**");
		
		//username maximum length validation
		resultMap = RegistrationHelper.validateUsername("Binson Selvin Binson Selvin Binson Selvin Binson Selvin Binson");
		assertEquals("Username must be less than or equal to 30 characters", resultMap.get("inputUsernameErr"), "Username length greater than 30 charcter should return error as **Username must be less than or equal to 30 characters**");
		
		//username maximum length validation
		resultMap = RegistrationHelper.validateUsername("2inson Selvin");
		assertEquals("Username cannot start with numeric", resultMap.get("inputUsernameErr"), "Username starting with numeric should return error as **Username cannot start with numeric**");
		
		//null username
		resultMap = RegistrationHelper.validateUsername(null);
		assertEquals("Username cannot be blank", resultMap.get("inputUsernameErr"), "Username null should return error as **Username cannot be blank**");
	}

	/**
	 * Test method for {@link com.sk.workitem.app.service.helper.RegistrationHelper#validateEmail(java.lang.String)}.
	 */
	@Test
	void testValidateEmail() {
		//valid email check
		Map<String, String> resultMap = RegistrationHelper.validateEmail("binson.selvin@skinternational.com");
		assertEquals(null, resultMap.get("inputEmailErr"), "Valid email should return null value as there is no error");
		
		//valid email check
		resultMap = RegistrationHelper.validateEmail("");
		assertEquals("Work Email cannot be blank", resultMap.get("inputEmailErr"), "Email null should return error as **Email address max length 320 characters**");
		
		//Email max length validation check
		resultMap = RegistrationHelper.validateEmail("binson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvinbinson.selvin@skinternational.com");
		assertEquals("Email address max length 320 characters", resultMap.get("inputEmailErr"), "Email null should return error as **Email address max length 320 characters**");
		
		//valid @skinternational email validation check
		resultMap = RegistrationHelper.validateEmail("binson.selvin@gmail.com");
		assertEquals("Please enter a valid email address", resultMap.get("inputEmailErr"), "Email null should return error as **Please enter a valid email address**");
		
		//valid @skinternational email validation check
		resultMap = RegistrationHelper.validateEmail(null);
		assertEquals("Work Email cannot be blank", resultMap.get("inputEmailErr"), "Email null should return error as **Work Email cannot be blank**");
	}
	
	/**
	 * Test method for {@link com.sk.workitem.app.service.helper.RegistrationHelper#validatePassword(java.lang.String)}.
	 */
	@Test
	void testValidatePassword() {
		//valid password check
		Map<String, String> resultMap = RegistrationHelper.validatePassword("Binson@123");
		assertEquals(null, resultMap.get("inputPasscodeErr"), "Valid password should return **null** value as there is no error");
		
		//blank password check
		resultMap = RegistrationHelper.validatePassword("");
		assertEquals("Password cannot be blank", resultMap.get("inputPasscodeErr"), "Valid password should return error as **Password cannot be blank**");
		
		//password not meeting criteria check
		resultMap = RegistrationHelper.validatePassword("root");
		assertEquals("password must contain one upper-case, special character and numeric and must be atleast 10 characters", resultMap.get("inputPasscodeErr"), "Valid password should return error as **password must contain one upper-case, special character and numeric and must be atleast 10 characters**");
		
		//password null
		resultMap = RegistrationHelper.validatePassword(null);
		assertEquals("Password cannot be blank", resultMap.get("inputPasscodeErr"), "Valid password should return error as **Password cannot be blank**");
	}

	/**
	 * Test method for {@link com.sk.workitem.app.service.helper.RegistrationHelper#validateConfirmPassword(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testValidateConfirmPassword() {
		//blank confirm password check
		Map<String, String> resultMap = RegistrationHelper.validateConfirmPassword("Binson@123", "");
		assertEquals("Confirm Password cannot be blank", resultMap.get("confPassError"), "Blank confirm password should return **Confirm Password cannot be blank** value as error");
		
		//valid confirm password
		resultMap = RegistrationHelper.validateConfirmPassword("Binson@123", "Binson@123");
		assertEquals(null, resultMap.get("confPassError"), "null confirm password should return **null** as error as there is no validation");
		
		//null confirm password
		resultMap = RegistrationHelper.validateConfirmPassword("Binson@123", null);
		assertEquals("Confirm Password cannot be blank", resultMap.get("confPassError"), "null confirm password should return **Confirm Password cannot be blank** value as error");
		
		//null confirm password
		resultMap = RegistrationHelper.validateConfirmPassword("Binson@123", "Binson@1234");
		assertEquals("Password and Confirm Password not matching", resultMap.get("confPassError"), "Blank confirm password should return **Password and Confirm Password not matching** value as error");
	}

}
