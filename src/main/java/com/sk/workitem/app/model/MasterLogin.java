package com.sk.workitem.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Data
@Entity(name = "mstr_logins")
public class MasterLogin {
	
	@Column(name = "user_id")
	private int userId;
	
	@Column(name = "org_id")
	private int orgId;
	
	@Column(name = "role_id")
	private int roleId;
	
	private String username;
	
	@Id
	@Column(name = "user_email")
	private String userEmail;
	
	@Column(name = "password_hash")
	private String passwordHash;
	
	@Column(name = "password_encrypted")
	private byte[] passEncrypted;
	
	private String salt;
	
	@Column(name = "mobile_number")
	private String mobileNum;
	
	@Column(name = "last_successful_login")
	private LocalDateTime lastSuccLogin;
	
	@Column(name = "last_unsuccessful_login")
	private LocalDateTime lastUnSuccLogin;
	
	@Column(name = "unsuccessful_login_count")
	private int failedCount;
	
	@Column(name = "account_locked")
	private boolean accLocked;
	
	@Column(name = "invalid_otp_attempt")
	private int invalidOtpCount;
	
	@Column(name = "last_valid_otp")
	private LocalDateTime lastValidOtp;
	
	@Column(name = "last_invalid_otp")
	private LocalDateTime lastInvalidOtp;
	
	@Column(name = "resend_count")
	private int resendCount;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "sk_branch", referencedColumnName = "branch_id")
	private MasterSKBranch skBranch;
	
	@Column(updatable = false, name = "created_by")
	private String createdBy;
	
	@Column(updatable = false, name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(insertable = false, name = "last_modified_by")
	private String modifiedBy;
	
	@Column(insertable = false, name = "last_modified_at")
	private LocalDateTime modifiedAt;
}
