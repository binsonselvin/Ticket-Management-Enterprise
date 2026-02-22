package com.sk.workitem.app.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LoginController.class)
class LoginControllerTest {
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	final void testLoadPage() {
		fail("Not yet implemented");
	}

	@Test
	final void testGetSecurityVerificationPage() {
		fail("Not yet implemented");
	}

	@Test
	final void testResendSecurityCode() {
		fail("Not yet implemented");
	}

	@Test
	final void testVerifySecurityCode() {
		fail("Not yet implemented");
	}

	@Test
	final void testGetLogoutPage() {
		fail("Not yet implemented");
	}

	@Test
	final void testGetLogoutVerificationPage() {
		fail("Not yet implemented");
	}

	@Test
	final void testGetResetPasswordPage() {
		fail("Not yet implemented");
	}

	@Test
	final void testResetAccountPasswordPage() {
		fail("Not yet implemented");
	}

}
