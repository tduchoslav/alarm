package com.tduch.alarm.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ALARM_SENT_EMAIL")
public class AlarmEmailInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private Integer id;
	
	//TODO prevest na long timestamp?
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sent_tmstmp")
	private Date sentTmstmp;
	
	@Column(name = "email_msg")
	private String emailInfo;
	
	
}
