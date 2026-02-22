package com.sk.workitem.app.errors;

/***
 * @author Binson Selvin
 * @since 03-August-2024
 * If the requested roleId from the Database is missing then this error is thrown
 */
@SuppressWarnings("serial")
public class RoleNotFoundException extends Exception {

	public RoleNotFoundException(String errorMssg) {
		super(errorMssg);
	}
	
	public RoleNotFoundException() {}
}
