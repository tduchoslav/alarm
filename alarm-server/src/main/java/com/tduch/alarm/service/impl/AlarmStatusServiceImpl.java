package com.tduch.alarm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tduch.alarm.AppConstants;
import com.tduch.alarm.entity.AlarmStatusEntity;
import com.tduch.alarm.repository.AlarmStatusRepository;
import com.tduch.alarm.service.AlarmStatusService;

@Service
public class AlarmStatusServiceImpl implements AlarmStatusService {

	@Autowired
    private AlarmStatusRepository alarmStatusRepository;
	
	public AlarmStatusEntity update(AlarmStatusEntity status) {
		return alarmStatusRepository.save(status);
	}

	public AlarmStatusEntity findById(int id) {
		return alarmStatusRepository.findOne(id);
	}

	public boolean isAlarmStatusOn() {
		AlarmStatusEntity entity = findById(AppConstants.APP_ID);
		return entity.isRunning();
	}

	public void setAlarmStatus(boolean running) {
		AlarmStatusEntity entity = findById(AppConstants.APP_ID);
		entity.setRunning(running);
		update(entity);
	}

}
