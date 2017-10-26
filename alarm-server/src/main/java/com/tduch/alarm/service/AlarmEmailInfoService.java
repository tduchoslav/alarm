package com.tduch.alarm.service;

import com.tduch.alarm.entity.AlarmEmailInfoEntity;

public interface AlarmEmailInfoService {

	AlarmEmailInfoEntity insert(AlarmEmailInfoEntity status);
	
	AlarmEmailInfoEntity findById(int id);
    
	/**
	 * Returns number of the emails sent this month.
	 * @return
	 */
	Integer getSentEmailsCountInCurrentMonth();
}
