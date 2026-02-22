package com.sk.workitem.app.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Component;

import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.repository.CustomTokenRepository;
import com.sk.workitem.app.repository.MasterLoginRepository;
import com.sk.workitem.app.repository.MasterRolesRepository;
import com.sk.workitem.app.service.helper.HttpHelper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class RememberMeServicesImpl implements RememberMeServices {

	private Logger log = LogManager.getLogger(getClass());
    private final CustomTokenRepository persistentTokenRepo;
    private final MasterRolesRepository roleRepo;
    private final MasterLoginRepository loginRepo;
    private final UserDetailsService userDetailsService;
    
    PersistentTokenBasedRememberMeServices obj;
    
    @Value("server.servlet.context-path")
    private String contextPath;

    @Autowired
    public RememberMeServicesImpl(CustomTokenRepository persistentTokenRepo, MasterLoginRepository loginRepo,
                                   UserDetailsService userDetailsService, 
                                   MasterRolesRepository roleRepo) {
        this.persistentTokenRepo = persistentTokenRepo;
        this.userDetailsService = userDetailsService;
        this.roleRepo = roleRepo;
        this.loginRepo = loginRepo;
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("In AutoLogin");
    	System.out.println("rememberMe checkbox value: "+request.getAttribute("rememberMe"));
        // Retrieve the remember-me token from the request
        String token = extractRememberMeCookie(request);
        
        if (token == null) {
            System.out.println("NO TOKEN FOUND: ");
            return null; 
        }

        PersistentRememberMeToken dbToken = persistentTokenRepo.getTokenForSeries(token);
        if (Objects.isNull(dbToken)) {
            return null;
        }

        // Validate the token
        if (dbToken.getSeries().equals(token)) {
        	System.out.println("Token values match: ");
        	MasterLogin masterLoginObj = loginRepo.findById(dbToken.getUsername()).get();
            UserDetails userDetails = userDetailsService.loadUserByUsername(dbToken.getUsername());
            if (userDetails != null) {
                // Get the user's role and create a new authentication token
            	String userRole = roleRepo.findById(masterLoginObj.getRoleId()).get().getRoleName();
            	log.info("UserDetails Not Null: {}",userDetails);
            	log.info("masterLoginObj Not Null: {}",masterLoginObj);
            	log.info("masterRoles Not Null: {}",userRole);
        		Authentication auth = new RememberMeAuthenticationToken(userDetails.getUsername(), null, getGrantedAuthority(userRole));
//        		SecurityContext context = SecurityContextHolder.createEmptyContext();
//        		context.setAuthentication(auth);
//        		SecurityContextHolder.setContext(context);
//        		securityContextRepository.saveContext(context, request, response);
//        		SecurityContextHolder.getContext().setAuthentication(auth);
        		return auth;
            }
        }
        return null;
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("LoginFail");
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
    	String isRememberMeChecked = request.getParameter("rememberMe");
    	log.info("Authentication is Success. Checkbox Value: {}",isRememberMeChecked);
    	if(Objects.nonNull(isRememberMeChecked)) {
    		PersistentRememberMeToken persistentToken = TokenGenerator.createToken((String)successfulAuthentication.getPrincipal());
    		//persistentTokenRepo.createNewToken(persistentToken);
    		new HttpHelper(persistentTokenRepo).storeCookieRememberMe(response, contextPath, persistentToken);
    	}
    }

    private String extractRememberMeCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("remember-me".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null; 
    }

    private List<GrantedAuthority> getGrantedAuthority(String role) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return grantedAuthorities;
    }
    
}