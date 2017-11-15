package com.tduch.alarm.service.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.dto.AlarmEmailInfoDto;
import com.tduch.alarm.entity.mongdb.AlarmEmailInfoMongoEntity;
import com.tduch.alarm.entity.sql.AlarmEmailInfoEntity;
import com.tduch.alarm.repository.mongodb.AlarmEmailInfoMongoRepository;
import com.tduch.alarm.repository.sql.AlarmEmailInfoRepository;
import com.tduch.alarm.service.AlarmEmailInfoService;

@Service
public class AlarmEmailInfoServiceImpl implements AlarmEmailInfoService {

	private final static Logger LOGGER = LoggerFactory.getLogger(AlarmEmailInfoServiceImpl.class);
	
	@Autowired
    private AlarmEmailInfoRepository alarmEmailInfoRepository;
	
	 @Autowired
	 private AlarmEmailInfoMongoRepository alarmEmailInfoMongoRepository;
	 
	 @Autowired
	 private AppProperties appProperties;
	
	public AlarmEmailInfoDto insert(AlarmEmailInfoDto emailInfo) {
		if (appProperties.isMongoDatasource()) {
			AlarmEmailInfoMongoEntity entity = alarmEmailInfoMongoRepository.save(emailInfo.getMongoEntityFromDto());
			return new AlarmEmailInfoDto(entity);
		} else {
			AlarmEmailInfoEntity entity = alarmEmailInfoRepository.save(emailInfo.getEntityFromDto());
			return new AlarmEmailInfoDto(entity);
		}
	}

//	public AlarmEmailInfoEntity findById(int id) {
//		return alarmEmailInfoRepository.findOne(id);
//	}

	public Integer getSentEmailsCountInCurrentMonth() {
		LocalDate today = new LocalDate();
		DateTime dateFrom = today.withDayOfMonth(1).toDateTimeAtStartOfDay();
		DateTime dateTo = today.plusMonths(1).withDayOfMonth(1).toDateTimeAtStartOfDay();
		LOGGER.debug("Date from {} to {}", dateFrom, dateTo);
		List<?> recordsInPeriod = null;
		if (appProperties.isMongoDatasource()) {
			recordsInPeriod = alarmEmailInfoMongoRepository.findBySentTmstmpBetween(dateFrom.toInstant().getMillis(), dateTo.toInstant().getMillis());
		} else {
			recordsInPeriod = alarmEmailInfoRepository.getRecordsInPeriod(dateFrom.toInstant().getMillis(), dateTo.toInstant().getMillis());
		}
		if (recordsInPeriod != null) {
			LOGGER.debug("Records in period: {}", recordsInPeriod.size());
			return recordsInPeriod.size();
		}
		return 0;
	}

}
