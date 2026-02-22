package com.sk.workitem.app.service.impl;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sk.workitem.app.constants.SystemUserConstant;
import com.sk.workitem.app.model.CustomerGroup;
import com.sk.workitem.app.repository.CustomerGroupRepository;

import com.sk.workitem.app.service.CustomerGroupService;

import jakarta.transaction.Transactional;


@Component
public class CustomerGroupServiceImpl implements CustomerGroupService {

	private CustomerGroupRepository customerGroupRepository;
	
	@Autowired
	public CustomerGroupServiceImpl(CustomerGroupRepository customerGroupRepository) {
		this.customerGroupRepository = customerGroupRepository;
		
	}
	
	// Validating Customer Group
	
	@Override
	public String validateCustomerGorup(String customerGroup) {
		
		if(customerGroup.equals("")) {
			
			return "Customer Group cannot be blank";
		}
		
		// Check Special Character at start
					if(!Pattern.compile("^(?![!#$%&'*+/=?^_`{|}~])[a-zA-Z0-9]").matcher(customerGroup).find()) {
						
						return "Customer Group cannot start with special characters";
					}

					// Check Special Character at end
					if(!Pattern.compile("(?<![!#$%&'*+/=?^_`{|}~])$").matcher(customerGroup).find()) {
						
						return "Customer Group cannot end with special characters";
					}
					
					// Check Numeric at start
					if(Pattern.compile("^[0-9]{1}").matcher(customerGroup).find()) {
						return "Customer Group cannot start with number ";
					}
		
		return null;
	}

	@Override
	@Transactional(rollbackOn = { Exception.class })
	public Boolean saveCustomeGroup(String customerGroup,String userEmail) {
		
		// Inserting Data in a Customer Group
		CustomerGroup cuGroup = new CustomerGroup();
		cuGroup.setCustomerGroupName(customerGroup);
		cuGroup.setCreatedBy(userEmail);
		cuGroup.setCreatedAt(LocalDateTime.now());
		customerGroupRepository.save(cuGroup);
		return true;
	}

	@Override
	public Boolean checkCustomerGroupExist(String customerGroup) {
		
		
		return customerGroupRepository.findByCustomerGroupName(customerGroup) == null ? false : true;
	}

}
