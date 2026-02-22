package com.sk.workitem.app.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.GeneratorType;
import org.hibernate.generator.Generator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Data
@Entity(name = "mstr_otp")
public class MasterOtp {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO, generator = "mstr_otp_otp_id_seq")
	@SequenceGenerator( allocationSize = 1, initialValue = 1, name = "mstr_otp_otp_id_seq", schema = "public"
			, sequenceName = "mstr_otp_otp_id_seq")
	@Column(name = "otp_id")
	private int otpId;
	@Column(name = "last_otp_generated_for")
	private String otpGenFor;
	@Column(name = "otp_generated")
	private String otpGenerated;
	@Column(name = "last_otp_generated")
	private LocalDateTime otpGenTime;
	@Column(name = "otp_expired")
	private boolean otpExpired;
	@Column(name = "last_otp_expire_time")
	private LocalDateTime otpExpireTime;
	@Column(name = "user_email")
	private String userEmail;
}
