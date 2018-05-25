package com.tduch.alarm.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.email.EmailSenderData;
import com.tduch.alarm.email.EmailSenderService;
import com.tduch.alarm.external.ExecuteShellComand;
import com.tduch.alarm.holder.AlarmInfoHolder;
import com.tduch.alarm.sms.SmsSenderData;
import com.tduch.alarm.sms.SmsSenderService;

@Service
public class DetectedMovementMonitor {

	private final static Logger LOGGER = LoggerFactory.getLogger(DetectedMovementMonitor.class);
	
	@Autowired
	AlarmInfoHolder alarmInfoHolder;
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private SmsSenderService smsSenderService;
	@Autowired
	private EmailSenderService emailSenderService;
	
	/**
	 * Runs just once after 1 minute when the method is called
	 */
	@Async
//	@Scheduled(fixedRate = Long.MAX_VALUE, initialDelay = 60000)
	public void checkMovementInfo() {
		LOGGER.info("Check if the alarm has been deactivated by user.");
		try {
			//waiting for 1 minute
			Thread.sleep(60000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		boolean isAlarmDeactivated = alarmInfoHolder.checkIfAlarmDeactivated();
		if (isAlarmDeactivated) {
			LOGGER.info("Alarm has been correctly deactivated since the movement was caught.");
			return;
		}
		LOGGER.info("Alarm has not been deactivated since the movement was caught. Send alarm email/sms.");
		
		SmsSenderData smsData = new SmsSenderData( "WARNING: Alarm server has not received deactivation request!");
		smsSenderService.send(smsData);
		EmailSenderData emailData = new EmailSenderData("movement detected but alarm has not been deactivated.");
		emailSenderService.send(emailData);
		
	}
	
	/**
	 * Runs shell commands to create picture snapshots from the camera
	 */
	@Async
	public void makePictureSnapshots(long currentTimeMillis) {
		if (appProperties.isCameraEnable()) {
			ExecuteShellComand.stopMotion();
			appProperties.getSnapshotsPrefix();
			String directory = appProperties.getSnapshotsDir();
			String fileName = ExecuteShellComand.getFileName(appProperties.getSnapshotsPrefix(), 
										currentTimeMillis, appProperties.getSnapshotsSuffix());
			ExecuteShellComand.snapshotImage(directory, fileName);	
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			//ExecuteShellComand.startMotion();
		} else {
			LOGGER.debug("No picture snapshots done, camera is disabled");
		}
	}
}
