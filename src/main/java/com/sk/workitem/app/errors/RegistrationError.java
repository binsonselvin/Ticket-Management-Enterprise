package com.sk.workitem.app.errors;

@SuppressWarnings("serial")
public class RegistrationError extends Exception{
	
	public RegistrationError(String errorMssg) {
		super(errorMssg);
	}
	
	public RegistrationError() {}
	
}
