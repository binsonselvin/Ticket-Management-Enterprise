package com.sk.workitem.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "persistent_logins")
public class PersistenceToken {
	@Id
	private Long id;
	private String username;
	private String series;
	@Column(name="last_used")
	private LocalDateTime date;
	@Column(name="token")
	private String tokenValue;
}
