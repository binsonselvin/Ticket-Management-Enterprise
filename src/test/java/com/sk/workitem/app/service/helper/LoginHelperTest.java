package com.sk.workitem.app.service.helper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LoginHelperTest {
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	void testValidateEmailAddress() {
		String validEmailAddress = "binson.selvin@skinternational.com";
		String emailEmpty = "";
		String invalidEmail = "binson.selvin@gmail.com";
		
		assertAll("Email Validation", 
			() -> assertEquals(true, LoginHelper.validateEmailAddress(validEmailAddress), () -> validEmailAddress+" is a valid email. expected true but actual value is false"),
			() -> assertEquals(false, LoginHelper.validateEmailAddress(emailEmpty), "Empty email address should return false"),
			() -> assertEquals(false,LoginHelper.validateEmailAddress(invalidEmail), "Invalid email address should return false")
		);
	}
	

}
