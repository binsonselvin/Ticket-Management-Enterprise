package com.sk.workitem.app.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.model.MasterRoles;
import com.sk.workitem.app.repository.MasterLoginRepository;
import com.sk.workitem.app.service.RegisterService;
import com.sk.workitem.app.service.helper.LoginHelper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/dashboard")
@CrossOrigin(origins="*")
public class DashboardController {
	
	public DashboardController() {}
	
	@GetMapping(path = {""} )
	public String getUsrDashboard(HttpServletRequest response, MasterLoginRepository loginRepo) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		return "admin-dashboard";
	}
}
