package com.tduch.alarm.entity.mongdb;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "alarm_sent_email")
public class AlarmEmailInfoMongoEntity implements Serializable {

	@Id
	private BigInteger id;

	private Long sentTmstmp;
	
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
