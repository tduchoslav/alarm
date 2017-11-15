package com.tduch.alarm.entity.mongdb;

import java.io.Serializable;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "alarm_status")
public class AlarmStatusMongoEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	
	private Boolean running;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
	

}
