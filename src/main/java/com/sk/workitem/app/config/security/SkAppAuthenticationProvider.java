package com.sk.workitem.app.config.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import com.sk.workitem.app.errors.RoleNotFoundException;
import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.model.MasterRoles;
import com.sk.workitem.app.repository.MasterLoginRepository;
import com.sk.workitem.app.repository.MasterRolesRepository;
import com.sk.workitem.app.service.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class SkAppAuthenticationProvider implements AuthenticationProvider {
	
	//Logger Initialization
	Logger log = LogManager.getLogger(getClass());
	
	private MasterLoginRepository loginRepo;
	private LoginService loginService;
	private MasterRolesRepository roleRepo;
	
	@Autowired
	HttpServletRequest httpRequest;
	
	@Autowired
	HttpSession session;
	
	@Autowired
	public SkAppAuthenticationProvider(MasterLoginRepository loginRepo, LoginService loginService,
			MasterRolesRepository roleRepo) {
		this.loginRepo = loginRepo;
		this.loginService = loginService;
		this.roleRepo = roleRepo;
	}
	
	/*
	 * Business logic for verifying password
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		MasterLogin usrObj = loginService.checkUserExists(username);
		
		if(usrObj != null) {
			if(usrObj.isAccLocked()) {
				throw new com.sk.workitem.app.errors.AccountLockedException("User Account has been locked. Kindly contact the adminstrator");
			} else {
				if(loginService.validateUserPwd(password, usrObj.getPasswordHash())){
					MasterRoles roles = roleRepo.findById(usrObj.getRoleId()).get();
					String roleName = roles != null ? roles.getRoleName() : null;
					if(Objects.isNull(roleName)) {
						throw new BadCredentialsException("Cannot determine the role of user");
					} else {
						System.out.println("rememberMe Value: "+session.getAttribute("rememberMe"));
						return new UsernamePasswordAuthenticationToken(usrObj.getUserEmail(), password, getGrantedAuthority(roleName));
					}
				} else {
					log.info("Username/Password is incorrect, try again");
					loginService.updateFailureLoginTimestamps(usrObj);
					throw new BadCredentialsException("Invalid Email or Password");
				}
			}
		} else {
			log.info("User {} does not exist", username);
			throw new UsernameNotFoundException("User does not exist");
		}
	}
	
	private List<GrantedAuthority> getGrantedAuthority(String role) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+role));
		return grantedAuthorities;
	}
	

	/*
	 * Check whether same Authentication class is getting used or not
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
