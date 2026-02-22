package com.sk.workitem.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sk.workitem.app.repository.CustomTokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LogoutController {
	
	private CustomTokenRepository tokenRepo;
	
	@Autowired
	public LogoutController(CustomTokenRepository tokenRepo) {
		this.tokenRepo = tokenRepo;
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response, RedirectAttributes model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
        	tokenRepo.removeUserTokens(auth.getName());
        	model.addAttribute("success", "You have been logged out successfully");
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout=true";
    }
}
