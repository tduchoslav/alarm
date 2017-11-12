package com.tduch.alarm.service.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tduch.alarm.entity.AlarmEmailInfoEntity;
import com.tduch.alarm.repository.AlarmEmailInfoRepository;
import com.tduch.alarm.service.AlarmEmailInfoService;

@Service
public class AlarmEmailInfoServiceImpl implements AlarmEmailInfoService {

	private final static Logger LOGGER = LoggerFactory.getLogger(AlarmEmailInfoServiceImpl.class);
	
	@Autowired
    private AlarmEmailInfoRepository alarmEmailInfoRepository;
	
	public AlarmEmailInfoEntity insert(AlarmEmailInfoEntity emailInfo) {
		return alarmEmailInfoRepository.save(emailInfo);
	}

	public AlarmEmailInfoEntity findById(int id) {
		return alarmEmailInfoRepository.findOne(id);
	}

	public Integer getSentEmailsCountInCurrentMonth() {
		LocalDate today = new LocalDate();
		DateTime dateFrom = today.withDayOfMonth(1).toDateTimeAtStartOfDay();
		DateTime dateTo = today.plusMonths(1).withDayOfMonth(1).toDateTimeAtStartOfDay();
		LOGGER.debug("Date from {} to {}", dateFrom, dateTo);
		List<AlarmEmailInfoEntity> recordsInPeriod = alarmEmailInfoRepository.getRecordsInPeriod(dateFrom.toInstant().getMillis(), dateTo.toInstant().getMillis());
		if (recordsInPeriod != null) {
			LOGGER.debug("Records in period: {}", recordsInPeriod.size());
			return recordsInPeriod.size();
		}
		return 0;
	}

}
