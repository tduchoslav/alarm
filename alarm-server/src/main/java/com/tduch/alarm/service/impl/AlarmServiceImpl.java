package com.tduch.alarm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.email.EmailSenderData;
import com.tduch.alarm.email.EmailSenderService;
import com.tduch.alarm.external.ExecuteShellComand;
import com.tduch.alarm.holder.AlarmInfoHolder;
import com.tduch.alarm.monitoring.DetectedMovementMonitor;
import com.tduch.alarm.service.AlarmService;
import com.tduch.alarm.service.AlarmSnapshotsService;
import com.tduch.alarm.service.AlarmStatusService;
import com.tduch.alarm.sms.SmsSenderData;
import com.tduch.alarm.sms.SmsSenderService;

@Service
public class AlarmServiceImpl implements AlarmService {

	private final static Logger LOGGER = LoggerFactory.getLogger(AlarmServiceImpl.class);
	
	@Autowired
	private AlarmInfoHolder alarmInfoHolder;
	
	@Autowired
	private AlarmStatusService alarmStatusService;
	
	@Autowired
	private DetectedMovementMonitor detectedMovementMonitor;
	
	@Autowired
	private AlarmSnapshotsService alarmSnapshotsService;
	
	@Autowired
	private AppProperties appProperties;
	
	
	@Autowired
	private SmsSenderService smsSenderService;
	@Autowired
	private EmailSenderService emailSenderService;
	
	@Override
	public void alarmHeartBeat() {
		LOGGER.info("Alarm heartbeat received.");
		alarmInfoHolder.setLastHeartBeatTimestamp(System.currentTimeMillis());
		//enable alarm if heart beats are being received and the alarm server have been down for some time.
		if (!alarmStatusService.isAlarmStatusOn()) {
			
			boolean isEnable = alarmInfoHolder.checkIfEnableAlarm();
			if (isEnable) {
				enableAlarm();
			}
		}
	}


	@Override
	public void disableAlarm() {
		LOGGER.info("Alarm disabled.");
		alarmInfoHolder.clearHeartBeats();
		alarmStatusService.setAlarmStatus(false);

		if (appProperties.isCameraEnable() && alarmInfoHolder.getLastDetectedMovementInfoTimestamp() != null) {
			String imageFileName = ExecuteShellComand.getFileName(appProperties.getSnapshotsPrefix(), 
					alarmInfoHolder.getLastDetectedMovementInfoTimestamp(), appProperties.getSnapshotsSuffix());
			ExecuteShellComand.deleteSnapshotFile(appProperties.getSnapshotsDir(), imageFileName);
		}
		
		alarmInfoHolder.resetDetectedMovementInfoTimestamp();
	}


	@Override
	public void enableAlarm() {
		LOGGER.info("Alarm enabled.");
		alarmInfoHolder.clearHeartBeats();
		alarmStatusService.setAlarmStatus(true);
	}


	@Override
	public void detectedMovement() {
		LOGGER.info("Alarm detected movement!!!");
		SmsSenderData smsData = new SmsSenderData("Movement detected warning.");
		smsSenderService.send(smsData);
		EmailSenderData emailData = new EmailSenderData("Movement detected warning.");
		emailSenderService.send(emailData);
				
		//disable alarm after movement is detected
		disableAlarm();
		
		//start making snapshot pictures
		if (appProperties.isCameraEnable()) {
			alarmSnapshotsService.snapshotPictures(appProperties.getSnapshotsInterval());
		}
		
	}


	@Override
	public boolean isAlarmEnabled() {
		boolean isAlarmOn = alarmStatusService.isAlarmStatusOn();
		LOGGER.info("Alarm is {}", isAlarmOn);
		return isAlarmOn;
	}


	@Override
	public boolean test() {
		LOGGER.info("Test if communication is connected.");
		return true;
	}


	@Override
	public void processVoltage(double currentVolts) {
		LOGGER.info("Alarm process voltage: " + currentVolts + "V.");
		if (currentVolts < 6.0) {
			SmsSenderData smsData = new SmsSenderData("WARN: ESP batteries are low !" + currentVolts + " V.");
			smsSenderService.send(smsData);
			EmailSenderData emailData = new EmailSenderData("WARN: ESP batteries are low !" + currentVolts + " V.");
			emailSenderService.send(emailData);
		}
		
	}

	@Override
	public void detectedMovementInfo() {
		LOGGER.info("Movement Info detected.");
		long currentTimeMillis = System.currentTimeMillis();
		alarmInfoHolder.setLastDetectedMovementInfoTimestamp(currentTimeMillis);
		detectedMovementMonitor.checkMovementInfo();
		detectedMovementMonitor.makePictureSnapshots(currentTimeMillis);
	}


	@Override
	public String getLogs() {
		// TODO Auto-generated method stub
		return "TODO";
	}	


	@Override
	public void stopMotionCamera() {
		LOGGER.info("Stop motion daemon.");
		ExecuteShellComand.stopMotion();
	}


	@Override
	public void startMotionCamera() {
		LOGGER.info("Start motion daemon.");
		ExecuteShellComand.startMotion();
	}

}