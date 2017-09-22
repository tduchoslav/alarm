package com.tduch.alarm.service;

import com.tduch.alarm.entity.AlarmStatusEntity;

public interface AlarmStatusService {

	AlarmStatusEntity update(AlarmStatusEntity status);
    AlarmStatusEntity findById(int id);
    
    boolean isAlarmStatusOn();
    void setAlarmStatus(boolean status);
    
}
