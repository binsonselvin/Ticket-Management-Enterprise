package com.sk.workitem.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sk.workitem.app.config.security.ProjectSecurityConfig;
import com.sk.workitem.app.constants.EmailTemplateConstant;
import com.sk.workitem.app.constants.SecurityCodeConstant;
import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.model.MasterOtp;
import com.sk.workitem.app.model.MasterRoles;
import com.sk.workitem.app.model.RestResponse;
import com.sk.workitem.app.repository.CustomTokenRepository;
import com.sk.workitem.app.repository.MasterLoginRepository;
import com.sk.workitem.app.repository.MasterRolesRepository;
import com.sk.workitem.app.service.LoginService;
import com.sk.workitem.app.service.RegisterService;
import com.sk.workitem.app.service.SecurityCodeService;
import com.sk.workitem.app.service.helper.HttpHelper;
import com.sk.workitem.app.service.helper.LoginHelper;
import com.sk.workitem.app.service.helper.RegistrationHelper;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(path = { "/login", "", "/" })
public class LoginController {

	Logger log = LogManager.getLogger(getClass());

	// model attribute key
	private static final String ERROR_KEY = "error";
	
	//application context path
	@Value("${server.servlet.context-path}")
	String CONTEXT_PATH;

	// session property key
	private static final String OTP_VERIFIED_KEY = "otp_verified";

	// resource path
	private static final String REDIRECT_DASHBOARD_PATH = "redirect:/dashboard";
	private static final String REDIRECT_RESET_PWD = "redirect:/login/forgotPassword/reset";
	private static final String REDIRECT_RESET_PWD_PATH = "redirect:/register";
	private static final String REDIRECT_LOGIN = "redirect:/login";

	private LoginService loginService;
	private SecurityCodeService securityCodeService;
	private MasterLoginRepository loginRepo;
	private RegisterService registerService;
	private HttpHelper httpHelper;
	private CustomTokenRepository customTokenRepository;
	private MasterRolesRepository rolesRepo;
	
	boolean isTokenUsed = false;

	@Autowired
	public LoginController(LoginService loginService, SecurityCodeService securityCodeService,
			MasterLoginRepository loginRepo, HttpHelper httpHelper, 
			CustomTokenRepository customTokenRepository, MasterRolesRepository rolesRepo) {
		this.loginService = loginService;
		this.securityCodeService = securityCodeService;
		this.loginRepo = loginRepo;
		this.httpHelper = httpHelper;
		this.customTokenRepository = customTokenRepository;
		this.rolesRepo = rolesRepo;
	}

	@RequestMapping(path = { "", "/" })
	public String loadPage(@RequestParam(required = false) String username,
			@RequestParam(required = false) String password, Model model, HttpServletRequest request,
			HttpServletResponse resp, @RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, HttpSession session) {

		log.info("Controller[LoginController][GET] /login Triggered");
		log.info("Remember Me: {}",request.getAttribute("rememberMe"));
		if (session.getAttribute(OTP_VERIFIED_KEY) != null) {
			return REDIRECT_DASHBOARD_PATH;
		}
		
//		if(readRememberMeCookie(request, resp)) {
//			// Check if user is already authenticated
//		    if (SecurityContextHolder.getContext().getAuthentication() != null 
//		        && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
//		    	System.out.println("He is a genuine user");
//		        return "redirect:/dashboard";
//		    }
//		}
		
		
		if (username == null) {
			return "login";
		}

		if (password == null) {
			return "login";
		}

		String errorMessage = null;
		if (error != null) {
			errorMessage = "Username or Password is incorrect !!";
		} else if (logout != null) {
			errorMessage = "You have been successfully logged out !!";
		}
		model.addAttribute(ERROR_KEY, errorMessage);

		return "/login?error=true";
	}

	@GetMapping(path = { "/verifyCode" })
	public String getSecurityVerificationPage(Model model, HttpSession session, HttpServletResponse response,
			@RequestParam(name = "otp_id", required = false) String otpId, 
			RedirectAttributes redirectModel ) throws MessagingException, IOException {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		String userEmailLbl = "userEmail";
		String successPage = "verifycode";
		log.info("Controller[LoginController][GET] /verifyCode Triggered");
		if (userEmail == null) {
			return REDIRECT_LOGIN;
		}

		if (session.getAttribute(OTP_VERIFIED_KEY) == null) {
			
			if(loginService.checkResendCountExceeded(userEmail)) {
				redirectModel.addFlashAttribute(ERROR_KEY, "User Account has been locked. Kindly contact the adminstrator");
				return REDIRECT_LOGIN;
			}
			
			
			if (securityCodeService.checkWhetherPreviousOtpExpired(userEmail)) {
				// Generate 6 digit security code using SecureRandom
				String securityCode = loginService.generateSecurityCode();
				// Add it to DB with expiration date.
				boolean isCodePersisted = securityCodeService.persistSecurityCode(securityCode,
						SecurityCodeConstant.LOGIN, userEmail);
				if (isCodePersisted) {
					securityCodeService.sendSecurityCodeViaEmailLogin(userEmail, securityCode,
							SecurityCodeConstant.LOGIN);
					model.addAttribute(userEmailLbl, userEmail);
					MasterOtp mstrOtp = loginService.getLastLoginOtp(SecurityCodeConstant.LOGIN, userEmail);
					model.addAttribute("otp_expire_time", mstrOtp.getOtpExpireTime());
					model.addAttribute("otp_id", mstrOtp.getOtpId());
					session.setAttribute("otp_expire_time", mstrOtp.getOtpExpireTime());
					session.setAttribute("otp_id", mstrOtp.getOtpId());
					//fetch otp timestamp and return to the front end
					return successPage;
				} else {
					model.addAttribute(userEmailLbl, userEmail);
					model.addAttribute(ERROR_KEY, "Unable to generated Security Code. Contact Administrator..!");
				}

				return successPage;
			} else {
				model.addAttribute(userEmailLbl, userEmail);
				MasterOtp mstrOtp = loginService.getLastLoginOtp(SecurityCodeConstant.LOGIN, userEmail);
				model.addAttribute("otp_expire_time", mstrOtp.getOtpExpireTime());
				model.addAttribute("otp_id", mstrOtp.getOtpId());
				session.setAttribute("otp_expire_time", mstrOtp.getOtpExpireTime());
				session.setAttribute("otp_id", mstrOtp.getOtpId());
				model.addAttribute(ERROR_KEY, "Otp has been sent already");
				return successPage;
			}
		} else {
			return REDIRECT_DASHBOARD_PATH;
		}
	}

	@RequestMapping(path = { "/verifyCode/rest" })
	@ResponseBody
	public ResponseEntity<RestResponse> resendSecurityCode(Model model, HttpSession session, 
			@RequestParam(name="otp_id", required = false) String otpId, @RequestParam(name="hiddenEmail", required = false) String hiddenEmail) throws MessagingException, IOException {
		log.info("Controller[LoginController][GET,POST] /verifyCode Triggered");
		log.warn("otp_received: {}",otpId);
		
		//for switching email context
		boolean isForReset = false;
		String accLockedMsg = "User Account has been locked. Kindly contact the adminstrator";
		
		RestResponse restResp = new RestResponse();
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("email: "+userEmail);
		
		if(userEmail.equals("anonymousUser")) {
			userEmail = hiddenEmail; 
			isForReset = true;
		}
		// Generate 6 digit security code using SecureRandom
		String securityCode = loginService.generateSecurityCode();
		if (userEmail == null) {
			restResp.setResponseCode("401");
			restResp.setResponseMsg("Please login to continue");
			return new ResponseEntity<>(restResp, HttpStatus.UNAUTHORIZED);
		}
		
		if(Objects.isNull(otpId) || otpId == "") {
			restResp.setResponseCode("400");
			restResp.setResponseMsg("otp_id cannot be blank or null");
			return new ResponseEntity<>(restResp, HttpStatus.BAD_REQUEST);
		}

		try {
			loginService.increaseResendCount(userEmail);
			if(loginService.checkResendCountExceeded(userEmail)) {
				log.error("Account Locked due to security code limit exceed");
				restResp.setResponseCode("401");
				restResp.setResponseMsg(accLockedMsg);
				//map for storing error
				Map<String, Object> respMap = new HashMap<>();
				respMap.put(ERROR_KEY, accLockedMsg);
				restResp.setObjData(respMap);
				return new ResponseEntity<>(restResp, HttpStatus.UNAUTHORIZED);
			}
			boolean isCodePersisted = false;
			if(!isForReset) {
				isCodePersisted = securityCodeService.persistSecurityCodeExpireOld(securityCode, SecurityCodeConstant.LOGIN,
					userEmail,otpId);
			} else {
				isCodePersisted = securityCodeService.persistSecurityCodeExpireOld(securityCode, SecurityCodeConstant.RESET,
						userEmail,otpId);
			}
			Map<String, Object> respMap = new HashMap<>();
			if (isCodePersisted) {

				if(loginService.checkResendCountExceeded(userEmail)) {
					log.error("Account Locked due to security code limit exceed");
					restResp.setResponseCode("401");
					restResp.setResponseMsg(accLockedMsg);
					//map for storing error
					respMap = new HashMap<>();
					respMap.put(ERROR_KEY, accLockedMsg);
					restResp.setObjData(respMap);
					return new ResponseEntity<>(restResp, HttpStatus.UNAUTHORIZED);
				}
				if(!isForReset) {
					securityCodeService.sendSecurityCodeViaEmailLogin(userEmail, securityCode, SecurityCodeConstant.LOGIN);
				} else {
					securityCodeService.sendSecurityCodeViaEmailReset(userEmail, securityCode, SecurityCodeConstant.RESET);
				}
				
				model.addAttribute("userEmail", userEmail);
				MasterOtp mstrOtp = loginService.getLastLoginOtp(SecurityCodeConstant.LOGIN, userEmail);
				model.addAttribute("otp_expire_time", mstrOtp.getOtpExpireTime());
				model.addAttribute("otp_id", mstrOtp.getOtpId());
				session.setAttribute("otp_expire_time", mstrOtp.getOtpExpireTime());
				session.setAttribute("otp_id", mstrOtp.getOtpId());
				restResp.setResponseCode("200");
				restResp.setResponseMsg("ok");
				//load new_otp_expire_time and otp_id
				respMap.put("otp_expire_time", mstrOtp.getOtpExpireTime());
				respMap.put("otp_id", mstrOtp.getOtpId());
				//add result to map
				restResp.setObjData(respMap);
				
				return new ResponseEntity<>(restResp, HttpStatus.OK);
			} else {
				restResp.setResponseCode("500");
				restResp.setResponseMsg("Cannot resend security code contact administrator..!");
				return new ResponseEntity<>(restResp, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception e) {
			restResp.setResponseCode("500");
			restResp.setResponseMsg("Cannot resend security code contact administrator..!");
			return new ResponseEntity<>(restResp, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(path = { "/verifyCode/submit" })
	public String verifySecurityCode(String securityCode, Model model, HttpSession session, 
			@RequestParam(name = "otp_id", required = false) String otpId,
			 @RequestParam("rememberMe") String rememberMe, HttpServletResponse response, HttpServletRequest request) throws MessagingException, IOException {
		log.info("Controller[LoginController][GET] /verifyCode/submit Triggered");

		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		if (userEmail == null) {
			return REDIRECT_LOGIN;
		}

		if (session.getAttribute(OTP_VERIFIED_KEY) == null) {
			String isCodeValid = securityCodeService.verifySecurityCode(SecurityCodeConstant.LOGIN, securityCode,
					userEmail, otpId);
			if (isCodeValid.equals("true")) {
				session.setAttribute(OTP_VERIFIED_KEY, "true");
				MasterLogin masterLogin = loginRepo.findById(userEmail).get();
//				System.out.println("Context-Path: "+CONTEXT_PATH);
//				System.out.println("rememberMe val before save cookie: "+session.getAttribute("rememberMe"));
//				if(rememberMe != null) {
//					httpHelper.storeCookieRememberMe(response, CONTEXT_PATH);
//					log.info("Cookie Saved");
//				}
				loginService.updateLoginTimestamps(masterLogin);
				return REDIRECT_DASHBOARD_PATH;
			} else {
				model.addAttribute(ERROR_KEY, isCodeValid);
				if(isCodeValid.equals("User Account has been locked. Due to max invalid attempt reached")) {
					model.addAttribute("account_locked", "true");
				}
				return "verifycode";
			}
		} else {
			return REDIRECT_DASHBOARD_PATH;
		}
	}

	@GetMapping("/forgotPassword")
	public String getLogoutPage() {
		log.info("Controller[LoginController][GET] /forgotPassword Triggered");
		return "forgot-password";
	}

	@GetMapping("/forgotPassword/verifyCode")
	public String getLogoutVerificationPage(String userEmail, Model model, HttpSession session, RedirectAttributes redirectModel)
			throws MessagingException, IOException {
		log.info("Controller[LoginController][GET] /forgotPassword/verifyCode Triggered");
		String verifyCodeResetPage = "verifycodeReset";
		String forgotPasswordPage = "forgot-password";

		if (userEmail != null) {
			if (userEmail.equals("")) {
				model.addAttribute(ERROR_KEY, "Email address cannot be empty");
				return forgotPasswordPage;
			} else if (!LoginHelper.validateEmailAddress(userEmail)) {
				model.addAttribute(ERROR_KEY, "Please enter a valid email address");
				return forgotPasswordPage;
			} else {
				model.addAttribute("email", userEmail);
				MasterLogin mstrLogin = loginService.checkUserExists(userEmail);
				System.out.println("mstr_Login: "+mstrLogin);
				if(Objects.isNull(mstrLogin)) {
					redirectModel.addFlashAttribute(ERROR_KEY, "User does not exist");
					return REDIRECT_LOGIN;
				} else {
					//check whether the account is locked or not
					if(mstrLogin.isAccLocked()) {
						System.out.println("Account is locked");
						redirectModel.addFlashAttribute(ERROR_KEY, "User Account has been locked. Kindly contact the adminstrator");
						return REDIRECT_LOGIN;
					}
				}
				
				if (securityCodeService.checkWhetherPreviousOtpExpiredReset(userEmail)) {
					// Generate 6 digit security code using SecureRandom
					String securityCode = loginService.generateSecurityCode();
					// Add it to DB with expiration date.
					boolean isCodePersisted = securityCodeService.persistSecurityCode(securityCode,
							SecurityCodeConstant.RESET, userEmail);
					if (isCodePersisted) {
						securityCodeService.sendSecurityCodeViaEmailReset(userEmail, securityCode,
								SecurityCodeConstant.RESET);
						model.addAttribute("userEmail", userEmail);
						session.setAttribute("userEmail", userEmail);
						MasterOtp mstrOtp = loginService.getLastLoginOtp(SecurityCodeConstant.RESET, userEmail);
						model.addAttribute("otp_expire_time", mstrOtp.getOtpExpireTime());
						model.addAttribute("otp_id", mstrOtp.getOtpId());
						session.setAttribute("otp_expire_time", mstrOtp.getOtpExpireTime());
						session.setAttribute("otp_id", mstrOtp.getOtpId());
						return verifyCodeResetPage;
					} else {
						model.addAttribute("userEmail", userEmail);
						model.addAttribute(ERROR_KEY, "Unable to generated Security Code. Contact Administrator..!");
					}
					return verifyCodeResetPage;
				} else {
					model.addAttribute("userEmail", userEmail);
					model.addAttribute(ERROR_KEY, "Otp has been sent already");
					return verifyCodeResetPage;
				}
			}
		} else {
			model.addAttribute(ERROR_KEY, "Email address cannot be empty");
			return forgotPasswordPage;
		}
	}

	@GetMapping(path = { "/forgotPassword/verifyCode/submit" })
	public String verifyResetSecurityCode(String securityCode, Model model, HttpSession session
			, String hiddenEmail, RedirectAttributes redirectModel, @RequestParam(name="otp_id", required = false) String otpId)throws MessagingException, IOException {
		log.info("Controller[LoginController][GET] /forgotPassword/verifyCode/submit Triggered");

		String userEmail = null;
		System.out.println("hiddenEmail while submitting code: "+hiddenEmail);
		if (Objects.isNull(hiddenEmail)) {
			return REDIRECT_LOGIN;
		} else {
			userEmail = hiddenEmail;
			redirectModel.addAttribute("userEmail", hiddenEmail);
			System.out.println("Redirecting model userEmail: "+hiddenEmail);
		}
		
		String isCodeValid = securityCodeService.verifySecurityCodeResetPwd(SecurityCodeConstant.RESET, securityCode,
				userEmail, otpId);

		if (isCodeValid.equals("true")) {
			System.out.println("Security Code is valid during reset");
			return REDIRECT_RESET_PWD;
		} else {
			model.addAttribute(ERROR_KEY, isCodeValid);
			if(isCodeValid.equals("User Account has been locked. Due to max invalid attempt reached")) {
				redirectModel.addFlashAttribute("error", "User Account has been locked. Due to max invalid attempt reached");
				return "redirect:/login";
			}
			return "verifycodeReset";
		}
	}
	
	@GetMapping("/forgotPassword/reset")
	public String getResetPasswordPage(@RequestParam("userEmail") String userEmail, Model model) {
		log.info("Controller[LoginController][GET] /forgotPassword/reset Triggered");
		System.out.println("Redirect Model userEmail: "+userEmail);
		model.addAttribute("userEmail", userEmail);
		if(Objects.isNull(userEmail)) {
			return REDIRECT_LOGIN;
		}
		return "Reset-Password";
	}
	
	@PostMapping("/forgotPassword/reset")
	public String resetAccountPasswordPage(String password, String confirmPassword, @RequestParam("hiddenEmail") String hiddenEmail 
			,Model model, RedirectAttributes redirectModel) {
		
		String resetPasswordPage = "Reset-Password";
		
		log.info("Controller[LoginController][POST] /forgotPassword/reset Triggered");

		log.info("hiddenEmail: {}",hiddenEmail);
		log.info("password: {}", password);
		log.info("confirm password: {}", confirmPassword);

		if(Objects.isNull(hiddenEmail)) {
			return REDIRECT_LOGIN;
		}
		
		Map<String, String> pwdValidationMap = RegistrationHelper.validatePassword(password);
		Map<String, String> confPwdValidationMap = RegistrationHelper.validateConfirmPassword(password, confirmPassword);

		if (!pwdValidationMap.isEmpty()) {
			model.addAttribute("inputPasscodeErr", pwdValidationMap.get("inputPasscodeErr"));
			return resetPasswordPage;
		}

		if(!confPwdValidationMap.isEmpty()) {
			model.addAttribute("confPassCodeError", confPwdValidationMap.get("confPassError"));
			return resetPasswordPage;
		}
		
		if(pwdValidationMap.isEmpty()) {
			//Business Knowledge to change password
			if(loginService.updateAccountPassword(hiddenEmail, password)) {
				redirectModel.addAttribute("success", "Your password has been updated successfully.");
				return REDIRECT_LOGIN;
			} else {
				redirectModel.addAttribute(ERROR_KEY, "Cannot reset password. Contact Administrator");
				return resetPasswordPage;
			}
		}

		return resetPasswordPage;
	}
	
	
	public boolean readRememberMeCookie(HttpServletRequest request, HttpServletResponse response) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("remember-me".equals(cookie.getName())) {
	                String encodedValue = cookie.getValue();
	                String[] parts = encodedValue.split(":");
	                if (parts.length == 2) {
	                    String series = parts[0];
	                    String tokenValue = parts[1];
	                    
	                    // Retrieve the token from your custom token repository
	                    PersistentRememberMeToken token = customTokenRepository.getTokenForSeries(series);
	                    if (token != null && token.getTokenValue().equals(tokenValue)) {
	                        // Retrieve user details
	                        MasterLogin mstrLogin = loginService.checkUserExists(token.getUsername());
	                        if (Objects.nonNull(mstrLogin)) {
	                            MasterRoles roles = rolesRepo.findById(mstrLogin.getRoleId()).orElse(null);
	                            if (roles != null) {
	                                Authentication authToken = new UsernamePasswordAuthenticationToken(
	                                    token.getUsername(), null, getGrantedAuthority(roles.getRoleName()));
	                                SecurityContextHolder.getContext().setAuthentication(authToken);
	                                
	                                // Optionally update the token if necessary
	                                customTokenRepository.updateToken(series, tokenValue, new Date());
	                                System.out.println("CRED: "+SecurityContextHolder.getContext().getAuthentication().getName());
	                                return true; // Successful authentication
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }
	    return false; // Authentication failed
	}
	
	private List<GrantedAuthority> getGrantedAuthority(String role) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+role));
		return grantedAuthorities;
	}
	
}