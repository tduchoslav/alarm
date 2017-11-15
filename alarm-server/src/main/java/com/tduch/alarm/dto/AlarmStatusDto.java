package com.tduch.alarm.dto;

import java.io.Serializable;

import com.tduch.alarm.entity.mongdb.AlarmStatusMongoEntity;
import com.tduch.alarm.entity.sql.AlarmStatusEntity;

public class AlarmStatusDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private Boolean running;
	
	public AlarmStatusDto() {
	}
	
	public AlarmStatusDto(AlarmStatusEntity entity) {
		this.id = entity.getId();
		this.running = entity.isRunning();
	}
	
	public AlarmStatusDto(AlarmStatusMongoEntity entity) {
		this.id = entity.getId();
		this.running = entity.isRunning();
	}

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
	
	public AlarmStatusEntity getEntityFromDto() {
		AlarmStatusEntity entity = new AlarmStatusEntity();
		entity.setId(this.id);
		entity.setRunning(this.running);
		return entity;
	}
	
	public AlarmStatusMongoEntity getMongoEntityFromDto() {
		AlarmStatusMongoEntity entity = new AlarmStatusMongoEntity();
		entity.setId(this.id);
		entity.setRunning(this.running);
		return entity;
	}
	

}
