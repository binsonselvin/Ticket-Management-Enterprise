package com.sk.workitem.app.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.sk.workitem.app.constants.EmailTemplateConstant;
import com.sk.workitem.app.constants.SecurityCodeConstant;
import com.sk.workitem.app.constants.SystemUserConstant;
import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.model.MasterOtp;
import com.sk.workitem.app.repository.MasterLoginRepository;
import com.sk.workitem.app.repository.MasterOtpRepository;
import com.sk.workitem.app.service.LoginService;
import com.sk.workitem.app.service.SecurityCodeService;

import jakarta.activation.FileTypeMap;
import jakarta.activation.URLDataSource;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import jakarta.persistence.criteria.From;
import lombok.extern.java.Log;

@Component
public class SecurityCodeServiceImpl implements SecurityCodeService {

	// logger instance
	private Logger log = LogManager.getLogger(getClass());

	// autowiring objects
	private MasterOtpRepository otpRepo;
	private MasterLoginRepository loginRepo;
	private LoginService loginService;

	// Mail Sending Object Declaration
	private JavaMailSender mailSender;

	@Autowired
	public SecurityCodeServiceImpl(MasterOtpRepository otpRepo, JavaMailSender mailSender,
			MasterLoginRepository loginRepo, LoginService loginService) {
		this.otpRepo = otpRepo;
		this.mailSender = mailSender;
		this.loginRepo = loginRepo;
		this.loginService = loginService;
	}

	@Override
	public boolean persistSecurityCode(String securityCode, String codeReqFor, String userEmail) {

		List<MasterOtp> mstrOtpList = otpRepo.findAllByOtpExpiredAndUserEmail(false,
				userEmail);
		if (expireAllOtp(mstrOtpList, otpRepo)) {
			LocalDateTime otpStartTime = LocalDateTime.now();
			LocalDateTime otpExpireTime = otpStartTime.plus(40, ChronoUnit.SECONDS);

			MasterOtp masterOtp = new MasterOtp();
			masterOtp.setOtpExpired(false);
			masterOtp.setOtpGenerated(securityCode);
			masterOtp.setOtpGenFor(codeReqFor);
			masterOtp.setOtpGenTime(otpStartTime);
			masterOtp.setOtpExpireTime(otpExpireTime);
			masterOtp.setUserEmail(userEmail);

			otpRepo.save(masterOtp);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean persistSecurityCodeExpireOld(String securityCode, String codeReqFor, String userEmail, String otpId) {

		if(Objects.isNull(otpId) || otpId == "" ) {
			return false;
		}
		
		try {
			int otpIdNew = Integer.parseInt(otpId);
			MasterOtp mstrOtp = otpRepo.findByUserEmailAndOtpIdNative(userEmail.trim(), otpIdNew);
			System.out.println("Mstr_Otp while reseting: "+mstrOtp);
			if(Objects.nonNull(mstrOtp)) {
			MasterOtp mstrOtpList = otpRepo.findFirstByOtpId(Integer.parseInt(otpId));
				if (expireOtp(mstrOtpList, otpRepo)) {
					LocalDateTime otpStartTime = LocalDateTime.now();
					LocalDateTime otpExpireTime = otpStartTime.plus(40, ChronoUnit.SECONDS);
		
					MasterOtp masterOtp = new MasterOtp();
					masterOtp.setOtpExpired(false); 
					masterOtp.setOtpGenerated(securityCode);
					masterOtp.setOtpGenFor(codeReqFor);
					masterOtp.setOtpGenTime(otpStartTime);
					masterOtp.setOtpExpireTime(otpExpireTime);
					masterOtp.setUserEmail(userEmail);
		
					otpRepo.save(masterOtp);
					return true;
				} else {
					return false;
				}
			} else {
				log.error("otp_id {} doesn't belong to the particular user.", otpId);
				return false;
			}
		} catch(Exception e) {
			log.error("Cannot expire otp by otp_id {}"+e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void sendSecurityCodeViaEmailLogin(String email, String securityCode, String codeReqFor)
			throws MessagingException, IOException {

		ClassPathResource sklogos = new ClassPathResource("/static/assets/Images/sklogo_email.png");
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper mimeHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_RELATED,
				"UTF-8");
		mimeHelper.addTo(email);
		mimeHelper.setReplyTo(EmailTemplateConstant.REPLY_TO);
		String body = "<html><body><img src='cid:sklogos' alt='sk' />\n"
				+ "<h4>Please use the following security code to login into the SK PMA</h4>\n"
				+ "<h3 style='color:gray;'> Security Code : " + securityCode + "</h3> \n"
				+ "<h4>Note: security code is valid for 15 minutes only</h4>" + "Thank You!<body></html>";
		mimeHelper.setSubject("Verify your identity - SK PMA");
		mimeHelper.setText(body, true);
		mimeHelper.addInline("sklogos", sklogos);

		mailSender.send(message);
	}

	@Override
	public void sendSecurityCodeViaEmailReset(String email, String securityCode, String codeReqFor)
			throws MessagingException, IOException {

		ClassPathResource sklogos = new ClassPathResource("/static/assets/Images/sklogo_email.png");
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper mimeHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_RELATED,
				"UTF-8");
		mimeHelper.addTo(email);
		mimeHelper.setReplyTo(EmailTemplateConstant.REPLY_TO);
		String body = "<html><body><img src='cid:sklogos' alt='sk' />\n"
				+ "<h4>Please use the following security code to reset password in SK PMA</h4>\n"
				+ "<h3 style='color:gray;'> Security Code : " + securityCode + "</h3> \n"
				+ "<h4>Note: security code is valid for 15 minutes only</h4>" + "Thank You!<body></html>";
		mimeHelper.setSubject("Reset your Password - SK PMA");
		mimeHelper.setText(body, true);
		mimeHelper.addInline("sklogos", sklogos);

		mailSender.send(message);

	}

	
	@Override
	public boolean checkWhetherPreviousOtpExpired(String email) {
		Sort sort = Sort.by(Direction.DESC ,"otpExpireTime");
		MasterOtp mstrOtp = otpRepo.findFirstByUserEmailAndOtpExpiredAndOtpGenFor(email, false, SecurityCodeConstant.LOGIN, sort);
		if (mstrOtp == null) {
			return true;
		}
		LocalDateTime expireTime = mstrOtp.getOtpExpireTime();
		LocalDateTime currTime = LocalDateTime.now();

		if (currTime.isBefore(expireTime))
			return false;

		return true;
	}

	@Override
	public boolean checkWhetherPreviousOtpExpiredReset(String email) {
		MasterOtp mstrOtp = otpRepo.findByUserEmailAndOtpExpiredAndOtpGenFor(email, false, SecurityCodeConstant.RESET);
		if (mstrOtp == null) {
			return true;
		}
		
		LocalDateTime expireTime = mstrOtp.getOtpExpireTime();
		LocalDateTime currTime = LocalDateTime.now();

		if (currTime.isBefore(expireTime))
			return false;

		return true;
	}
	
	@Override
	public boolean expireAllOtp(List<MasterOtp> mstrOtpList, MasterOtpRepository otpRepo) {

		try {
			mstrOtpList.forEach(data -> data.setOtpExpired(true));
			otpRepo.saveAll(mstrOtpList);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unable to expire security code");
			return false;
		}

		return true;
	}
	
	@Override
	public boolean expireOtp(MasterOtp mstrOtpObj, MasterOtpRepository otpRepo) {
		try {
			mstrOtpObj.setOtpExpired(true);
			otpRepo.save(mstrOtpObj);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unable to expire security code");
			return false;
		}
	}

	@Override
	public String verifySecurityCode(String codeReqFor, String securityCode, String userEmail, String otpId) {
		final String globalError = "Unable to verify code. Contact Administrator";
		
		//for storing MasterOtp Object
		MasterOtp mstrOtp = null;
		int oldOtp = 0;
		try {
			int otpIdInt = Integer.parseInt(otpId);
			mstrOtp = otpRepo.findByUserEmailAndOtpExpiredAndOtpGenForAndOtpId(userEmail, false, codeReqFor, otpIdInt);
		} catch(Exception e) {
			log.error(globalError, e.getLocalizedMessage());
			return globalError;
		}
		
		boolean isCodeForPassReset = false;
		
		oldOtp = Integer.parseInt(otpId);
		
		if (mstrOtp == null) {
			return "Unable to verify code. Contact Administrator";
		} else if(mstrOtp.getOtpId() == oldOtp) {
			LocalDateTime expireTime = mstrOtp.getOtpExpireTime();
			LocalDateTime currTime = LocalDateTime.now();

			MasterLogin mstrLogin = loginRepo.findById(userEmail).get();
			if (!mstrLogin.isAccLocked()) {
				if (currTime.isBefore(expireTime)) {
					log.info("Security Code is not expired");
					if (mstrOtp.getOtpGenerated().equals(securityCode)) {
						log.info("Valid Security Code");
						mstrOtp.setOtpExpired(true);
						otpRepo.save(mstrOtp);
						return "true";
					} else {
						log.info("Invalid Security Code");
						if (mstrLogin.getInvalidOtpCount() < 3) {
							mstrLogin.setInvalidOtpCount(mstrLogin.getInvalidOtpCount() + 1);
							if (mstrLogin.getInvalidOtpCount() == 3) {
								mstrLogin.setAccLocked(true);
								mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
								mstrLogin.setModifiedAt(currTime);
								mstrLogin.setLastInvalidOtp(currTime);
								log.info("User Account has been locked. Kindly contact the adminstrator");
								return "User Account has been locked. Due to max invalid attempt reached";
							}
						}
						mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
						mstrLogin.setModifiedAt(currTime);
						mstrLogin.setLastInvalidOtp(currTime);
						loginRepo.save(mstrLogin);
						return "Invalid Security Code";
					}
				} else {
					log.info("Invalid Security Code");
					if (mstrLogin.getInvalidOtpCount() < 3) {
						mstrLogin.setInvalidOtpCount(mstrLogin.getInvalidOtpCount() + 1);
						if (mstrLogin.getInvalidOtpCount() == 3) {
							mstrLogin.setAccLocked(true);
							mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
							mstrLogin.setModifiedAt(currTime);
							mstrLogin.setLastInvalidOtp(currTime);
							log.info("User Account has been locked. Kindly contact the adminstrator");
							return "User Account has been locked. Due to max invalid attempt reached";
						}
					}
					mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
					mstrLogin.setModifiedAt(currTime);
					mstrLogin.setLastInvalidOtp(currTime);
					loginRepo.save(mstrLogin);
					return "Invalid Security Code";
				}
			} else {
				log.info("User Account has been locked. Kindly contact the adminstrator");
				return "User Account has been locked. Due to max invalid attempt reached";
			}
		} else {
			log.info("Unable to verify code. Contact Administrator. Else condition");
			return "Unable to verify code. Contact Administrator";
		}
	}

	@Override
	public String verifySecurityCodeResetPwd(String codeReqFor, String securityCode, String userEmail, String otpId) {
		MasterOtp mstrOtp = null;
		try {
			mstrOtp = otpRepo.findFirstByOtpId(Integer.parseInt(otpId));
		} catch(NumberFormatException e) {
			log.info("Unable to convert otpId: {}",otpId);
			e.printStackTrace();
		}
		if (mstrOtp == null) {
			return "Unable to verify code. Contact Administrator";
		} else {
			
			if(!mstrOtp.getUserEmail().equals(userEmail)) {
				log.info("otpId does not belong to the user: {}",otpId);
				return "Unable to verify code. Contact Administrator";
			}
			
			LocalDateTime expireTime = mstrOtp.getOtpExpireTime();
			LocalDateTime currTime = LocalDateTime.now();

			MasterLogin mstrLogin = loginRepo.findById(userEmail).get();
			if (!mstrLogin.isAccLocked()) {
				if (currTime.isBefore(expireTime)) {
					log.info("Security Code is not expired");
					if (mstrOtp.getOtpGenerated().equals(securityCode)) {
						log.info("Valid Security Code");
						mstrOtp.setOtpExpired(true);
						otpRepo.save(mstrOtp);
						return "true";
					} else {
						log.info("Invalid Security Code");
						if (mstrLogin.getInvalidOtpCount() < 3) {
							mstrLogin.setInvalidOtpCount(mstrLogin.getInvalidOtpCount() + 1);
							if (mstrLogin.getInvalidOtpCount() == 3) {
								mstrLogin.setAccLocked(true);
								mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
								mstrLogin.setModifiedAt(currTime);
								mstrLogin.setLastInvalidOtp(currTime);
								log.info("User Account has been locked. Kindly contact the adminstrator");
								return "User Account has been locked. Due to max invalid attempt reached";
							}
						}
						mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
						mstrLogin.setModifiedAt(currTime);
						mstrLogin.setLastInvalidOtp(currTime);
						loginRepo.save(mstrLogin);
						return "Invalid Security Code";
					}
				} else {
					log.info("Invalid Security Code");
					if (mstrLogin.getInvalidOtpCount() < 3) {
						mstrLogin.setInvalidOtpCount(mstrLogin.getInvalidOtpCount() + 1);
						if (mstrLogin.getInvalidOtpCount() == 3) {
							mstrLogin.setAccLocked(true);
							mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
							mstrLogin.setModifiedAt(currTime);
							mstrLogin.setLastInvalidOtp(currTime);
							log.info("User Account has been locked. Kindly contact the adminstrator");
							return "User Account has been locked. Due to max invalid attempt reached";
						}
					}
					mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
					mstrLogin.setModifiedAt(currTime);
					mstrLogin.setLastInvalidOtp(currTime);
					loginRepo.save(mstrLogin);
					return "Invalid Security Code";
				}
			} else {
				log.info("User Account has been locked. Kindly contact the adminstrator");
				return "User Account has been locked. Due to max invalid attempt reached";
			}
		}
	}
}
