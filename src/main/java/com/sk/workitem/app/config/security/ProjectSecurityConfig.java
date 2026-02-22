package com.sk.workitem.app.config.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.sk.workitem.app.config.security.filter.RememberMeAuthenticationFilter;
import com.sk.workitem.app.repository.CustomTokenRepository;
import com.sk.workitem.app.service.impl.RememberMeServicesImpl;
import com.sk.workitem.app.service.impl.UserDetailsImpl;

/***
 * @author Binson Selvin
 * @since 03-August-2024
 * 
 * Security configuration for the application, setting up rules for access control,
 * CSRF protection, and login/logout handling.
 */
@Configuration
@PropertySource(name = "application.properties", value="classpath:application.properties")
public class ProjectSecurityConfig {
	
	@Value("${rememberMeKey}")
	public String key;
	
    private UserDetailsImpl userDetailsService;
	private RememberMeServicesImpl rememberMeServices;
	private DataSource dataSource;
	private CustomTokenRepository customTokenRepository;
	
	@Autowired
	public ProjectSecurityConfig(UserDetailsImpl userDetailsService, @Lazy RememberMeServicesImpl rememberMeServices
			,DataSource dataSource, CustomTokenRepository customTokenRepository, SkAppAuthenticationProvider authProvider) {
		this.userDetailsService = userDetailsService;
		this.rememberMeServices = rememberMeServices;
		this.dataSource = dataSource;
		this.customTokenRepository = customTokenRepository;
	}
	
	/***
     * Configures security settings including path authorization, CSRF protection, and login/logout handling.
     * 
     * @param http The HttpSecurity object used to configure web-based security for specific HTTP requests.
     * @param introspector Helper class to introspect the {@code HandlerMapping} and match request paths.
     * @return A {@link SecurityFilterChain} bean for security filter chain configuration.
     * @throws Exception If any configuration error occurs.
     */
	@Bean
	SecurityFilterChain configureSecurityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
			throws Exception {
		//MvcRequestMatcher uses Spring MVC's HandlerMappingIntrospector to match the path and extract variables.
		MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

		http.csrf( csrf -> csrf.ignoringRequestMatchers(mvcMatcherBuilder.pattern("/registerUser"))
				.ignoringRequestMatchers(new AntPathRequestMatcher("/register"))
				.ignoringRequestMatchers(new AntPathRequestMatcher("/register/verifyCode/**"))
				.ignoringRequestMatchers(new AntPathRequestMatcher("/assets/**"))
				.ignoringRequestMatchers(new AntPathRequestMatcher("/static/**"))
				.ignoringRequestMatchers(new AntPathRequestMatcher("/login"))
				.ignoringRequestMatchers(new AntPathRequestMatcher("/login/forgotPassword"))
				.ignoringRequestMatchers(new AntPathRequestMatcher("/login/forgotPassword/reset"))
				.ignoringRequestMatchers("/reset/default/**"))
				.authorizeHttpRequests(requests -> 
						requests.requestMatchers("/").permitAll()
						.requestMatchers(mvcMatcherBuilder.pattern("/register")).permitAll()
						.requestMatchers(mvcMatcherBuilder.pattern("/register/verifyCode/**")).permitAll()
						.requestMatchers(mvcMatcherBuilder.pattern("/register/registerOrg")).permitAll()
						.requestMatchers(mvcMatcherBuilder.pattern("/assets/**")).permitAll()
						.requestMatchers(mvcMatcherBuilder.pattern("/login")).permitAll()
						.requestMatchers(mvcMatcherBuilder.pattern("/dashboard/**")).authenticated()
						.requestMatchers(mvcMatcherBuilder.pattern("/login/verifyCode")).authenticated()
						.requestMatchers(mvcMatcherBuilder.pattern("/login/forgotPassword")).permitAll()
						.requestMatchers(mvcMatcherBuilder.pattern("/login/forgotPassword/reset")).permitAll()
						.requestMatchers(mvcMatcherBuilder.pattern("/login/forgotPassword/verifyCode")).permitAll()
						.requestMatchers(mvcMatcherBuilder.pattern("/login/forgotPassword/verifyCode/submit")).permitAll()
						.requestMatchers(mvcMatcherBuilder.pattern("/login/verifyCode/rest")).permitAll()
						.requestMatchers(mvcMatcherBuilder.pattern("/login/reset")).permitAll()
						//Admin dashboard protected URLs
						.requestMatchers(mvcMatcherBuilder.pattern("/admin")).hasRole("PORTAL_OWNER")
						.requestMatchers(mvcMatcherBuilder.pattern("/admin/**")).hasRole("PORTAL_OWNER")
						.requestMatchers(mvcMatcherBuilder.pattern("/admin/project/view")).hasRole("PORTAL_OWNER")
						.requestMatchers(mvcMatcherBuilder.pattern("/admin/customergroup")).hasRole("PORTAL_OWNER")
						.requestMatchers(mvcMatcherBuilder.pattern("/admin/project/relatedCustomer")).hasRole("PORTAL_OWNER")
						.requestMatchers(mvcMatcherBuilder.pattern("/admin/project/relatedActivity")).hasRole("PORTAL_OWNER")
						.requestMatchers(mvcMatcherBuilder.pattern("/admin/project/relatedManager")).hasRole("PORTAL_OWNER")
						.requestMatchers(mvcMatcherBuilder.pattern("/admin/project/save")).hasRole("PORTAL_OWNER")
						.anyRequest().authenticated()
						)
				.formLogin(loginConfigurer -> loginConfigurer
						.loginPage("/login").permitAll()
						.defaultSuccessUrl("/login/verifyCode", true)
						.failureUrl("/login?error=true").permitAll())
				.logout(logoutConfigurer -> logoutConfigurer
						.logoutSuccessUrl("/login?logout=true")
						.deleteCookies("remember-me")
						//clear the session cookies on logout
						.addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(Directive.COOKIES)))
						.invalidateHttpSession(true).permitAll())
		        .sessionManagement(sessionManagement -> sessionManagement.maximumSessions(2).and()
		                .sessionFixation().migrateSession());
//		        .rememberMe( rememberMe -> rememberMe
//		        		.rememberMeParameter("rememberMe")
//		        		.key(key)
//		        		.tokenRepository(customTokenRepository)
//		        		.userDetailsService(userDetailsService)
//		        		.tokenValiditySeconds(300)
//		        		.rememberMeServices(rememberMeServices));
		return http.build();
	}
	
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}
	
}