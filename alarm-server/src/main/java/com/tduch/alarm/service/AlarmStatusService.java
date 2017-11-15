package com.tduch.alarm.service;

public interface AlarmStatusService {

//	AlarmStatusEntity update(AlarmStatusEntity status);
//    AlarmStatusEntity findById(int id);
    
    boolean isAlarmStatusOn();
    void setAlarmStatus(boolean status);
    
}
