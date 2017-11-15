package com.tduch.alarm.entity.sql;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ALARM_SENT_EMAIL")
public class AlarmEmailInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private BigInteger id;

	@Column(name = "sent_tmstmp")
	private Long sentTmstmp;
	
	@Column(name = "email_msg")
	private String emailInfo;

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public Long getSentTmstmp() {
		return sentTmstmp;
	}

	public void setSentTmstmp(Long sentTmstmp) {
		this.sentTmstmp = sentTmstmp;
	}

	public String getEmailInfo() {
		return emailInfo;
	}

	public void setEmailInfo(String emailInfo) {
		this.emailInfo = emailInfo;
	}
	
	
}
