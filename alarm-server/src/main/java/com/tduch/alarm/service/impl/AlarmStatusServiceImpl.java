package com.tduch.alarm.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tduch.alarm.AppConstants;
import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.entity.mongdb.AlarmStatusMongoEntity;
import com.tduch.alarm.entity.sql.AlarmStatusEntity;
import com.tduch.alarm.repository.mongodb.AlarmStatusMongoRepository;
import com.tduch.alarm.repository.sql.AlarmStatusRepository;
import com.tduch.alarm.service.AlarmStatusService;

@Service
public class AlarmStatusServiceImpl implements AlarmStatusService {

	@Autowired
    private AlarmStatusRepository alarmStatusRepository;
	
	@Autowired
	private AlarmStatusMongoRepository alarmStatusMongoRepository;

	@Autowired
	private AppProperties appProperties;

//	public AlarmStatusDto update(AlarmStatusDto status) {
//		if (appProperties.isMongoDatasource()) {
//			// todo map to mongo entity
//			return saveMongo(status);
//		} else {
//			// map to sql entity
//			return saveSQL(status);
//		}
//	}

	private void saveMongo(AlarmStatusMongoEntity entity) {
		alarmStatusMongoRepository.save(entity);
	}

	private void saveSQL(AlarmStatusEntity entity) {
		alarmStatusRepository.save(entity);
	}

	// public AlarmStatusDto findById(int id) {
	// return alarmStatusRepository.findOne(id);
	// }

	public boolean isAlarmStatusOn() {
		if (appProperties.isMongoDatasource()) {
			Optional<AlarmStatusMongoEntity> entity = alarmStatusMongoRepository.findById(AppConstants.APP_ID);
			return (entity.isPresent()) ? entity.get().isRunning() : false;
		} else {
			Optional<AlarmStatusEntity> entity = alarmStatusRepository.findById(AppConstants.APP_ID);
			return (entity.isPresent()) ? entity.get().isRunning() : false;
		}
	}

	public void setAlarmStatus(boolean running) {
		if (appProperties.isMongoDatasource()) {
			Optional<AlarmStatusMongoEntity> entity = alarmStatusMongoRepository.findById(Integer.valueOf(AppConstants.APP_ID));
			entity.get().setRunning(running);
			saveMongo(entity.get());
		} else {
			Optional<AlarmStatusEntity> entity = alarmStatusRepository.findById(AppConstants.APP_ID);
			entity.get().setRunning(running);
			saveSQL(entity.get());
		}
	}

}
