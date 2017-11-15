package com.tduch.alarm.service;

import com.tduch.alarm.dto.AlarmEmailInfoDto;

public interface AlarmEmailInfoService {

	AlarmEmailInfoDto insert(AlarmEmailInfoDto emailInfo);
	
//	AlarmEmailInfoEntity findById(int id);
    
	/**
	 * Returns number of the emails sent this month.
	 * @return
	 */
	Integer getSentEmailsCountInCurrentMonth();
}
