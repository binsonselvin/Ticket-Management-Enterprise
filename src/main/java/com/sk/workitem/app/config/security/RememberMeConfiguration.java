package com.sk.workitem.app.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices.RememberMeTokenAlgorithm;

public class RememberMeConfiguration {

	private final UserDetailsService userDetailsService;
	private AuthenticationManager authManager;
	
	@Autowired
	private SkAppAuthenticationProvider authenticationProvider;
	
	@Autowired
    public RememberMeConfiguration(UserDetailsService userDetailsService, AuthenticationManager authManager) {
        this.userDetailsService = userDetailsService;
        this.authManager = authManager;
    }
	
	//RememberMe Authentication
//	@Bean
//    RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
//        return new RememberMeAuthenticationProvider("Cynsx1FtWKbajSTOiBEtkfP7cfLngOAQEFlDF1LpLeU6CZv8L6kulu6obGRuq6e04JeH3rdPkEuLCEwRCVyPypU91DvRhyVrtnS5kruVoehUBwIuwUV5vg71yi35CgFJ");
//    }
    
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        authenticationManagerBuilder.authenticationProvider( new RememberMeAuthenticationProvider("Cynsx1FtWKbajSTOiBEtkfP7cfLngOAQEFlDF1LpLeU6CZv8L6kulu6obGRuq6e04JeH3rdPkEuLCEwRCVyPypU91DvRhyVrtnS5kruVoehUBwIuwUV5vg71yi35CgFJ"));
        return authenticationManagerBuilder.build();
    }
    
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    
//    @Bean
//    RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
//    	RememberMeTokenAlgorithm encodingAlgorithm = RememberMeTokenAlgorithm.SHA256;
//    	TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices("Cynsx1FtWKbajSTOiBEtkfP7cfLngOAQEFlDF1LpLeU6CZv8L6kulu6obGRuq6e04JeH3rdPkEuLCEwRCVyPypU91DvRhyVrtnS5kruVoehUBwIuwUV5vg71yi35CgFJ", userDetailsService, encodingAlgorithm);
//    	rememberMe.setMatchingAlgorithm(RememberMeTokenAlgorithm.MD5);
//    	rememberMe.setTokenValiditySeconds(600);
//    	return rememberMe;
//    }
    
}
