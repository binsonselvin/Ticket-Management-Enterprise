package com.sk.workitem.app.service;

public interface CustomerGroupService {

	String validateCustomerGorup(String customerGroup);

	Boolean saveCustomeGroup(String customerGroup,String userEmail);

	Boolean checkCustomerGroupExist(String customerGroup);

}
