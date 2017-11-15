package com.tduch.alarm.dto;

import java.io.Serializable;
import java.math.BigInteger;

import com.tduch.alarm.entity.mongdb.AlarmEmailInfoMongoEntity;
import com.tduch.alarm.entity.sql.AlarmEmailInfoEntity;

public class AlarmEmailInfoDto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BigInteger id;

	private Long sentTmstmp;
	
	private String emailInfo;

	public AlarmEmailInfoDto() {
	}
	
	public AlarmEmailInfoDto(AlarmEmailInfoEntity entity) {
		this.id = entity.getId();
		this.sentTmstmp = entity.getSentTmstmp();
		this.emailInfo = entity.getEmailInfo();
	}
	
	public AlarmEmailInfoDto(AlarmEmailInfoMongoEntity entity) {
		this.id = entity.getId();
		this.sentTmstmp = entity.getSentTmstmp();
		this.emailInfo = entity.getEmailInfo();
	}
	
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
	
	public AlarmEmailInfoEntity getEntityFromDto() {
		AlarmEmailInfoEntity entity = new AlarmEmailInfoEntity();
		entity.setId(this.id);
		entity.setEmailInfo(this.emailInfo);
		entity.setSentTmstmp(this.sentTmstmp);
		return entity;
	}
	
	public AlarmEmailInfoMongoEntity getMongoEntityFromDto() {
		AlarmEmailInfoMongoEntity entity = new AlarmEmailInfoMongoEntity();
		entity.setId(this.id);
		entity.setEmailInfo(this.emailInfo);
		entity.setSentTmstmp(this.sentTmstmp);
		return entity;
	}
}
