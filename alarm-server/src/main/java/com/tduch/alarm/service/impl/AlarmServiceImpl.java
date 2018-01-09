package com.tduch.alarm.service.impl;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.conf.EmailParameters;
import com.tduch.alarm.conf.SmsParameters;
import com.tduch.alarm.dto.AlarmEmailInfoDto;
import com.tduch.alarm.email.EmailUtil;
import com.tduch.alarm.external.ExecuteShellComand;
import com.tduch.alarm.holder.AlarmInfoHolder;
import com.tduch.alarm.monitoring.DetectedMovementMonitor;
import com.tduch.alarm.service.AlarmEmailInfoService;
import com.tduch.alarm.service.AlarmService;
import com.tduch.alarm.service.AlarmStatusService;
import com.tduch.alarm.sms.SmsUtil;

@Service
public class AlarmServiceImpl implements AlarmService {

	private final static Logger LOGGER = LoggerFactory.getLogger(AlarmServiceImpl.class);
	
	@Autowired
	private AlarmInfoHolder alarmInfoHolder;
	
	@Autowired
	private AlarmStatusService alarmStatusService;
	
	@Autowired
	private AlarmEmailInfoService alarmEmailInfoService;
	
	@Autowired
	private DetectedMovementMonitor detectedMovementMonitor;
	
	@Autowired
	private AppProperties appProperties;
	
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
		if (appProperties.isSmsEnable()) {
			try {
				SmsParameters smsParameters = new SmsParameters(appProperties.getSmsAccountSid(), 
						appProperties.getSmsAccountAuthToken(), appProperties.getPhoneNumberTo(), appProperties.getPhoneNumberFrom());
				if (appProperties.isSmsEnable()) {
					SmsUtil.sendSms(smsParameters, "ALARM: Movement detected in our house!");
				}
			} catch (Exception e) {
				LOGGER.error("Could not send SMS.", e);
			}
		}
		if (appProperties.isEmailEnable()) {
			try {
				EmailParameters emailParameters = null;
				if (alarmEmailInfoService.getSentEmailsCountInCurrentMonth() < appProperties.getMaxNumEmailsPerMonth()) {
					emailParameters = new EmailParameters(appProperties.getEmailFrom(), 
						appProperties.getEmailFromPassword(), appProperties.getEmailTo());
				} else {
					//max limit exceeded, send email to backup email
					emailParameters = new EmailParameters(appProperties.getEmailFrom2(), 
							appProperties.getEmailFromPassword2(), appProperties.getEmailTo2());
				}
				if (appProperties.isEmailEnable()) {
//					AlarmEmailInfoEntity emailInfoEtity = new AlarmEmailInfoEntity();
//					emailInfoEtity.setEmailInfo("movement detected warning.");
//					emailInfoEtity.setSentTmstmp(System.currentTimeMillis());
					AlarmEmailInfoDto emailInfoDto = new AlarmEmailInfoDto();
					emailInfoDto.setEmailInfo("Movement detected warning.");
					emailInfoDto.setSentTmstmp(System.currentTimeMillis());
					alarmEmailInfoService.insert(emailInfoDto);
					
					boolean cameraEnable = appProperties.isCameraEnable();
					String directory = appProperties.getSnapshotsDir();
					if (alarmInfoHolder.getLastDetectedMovementInfoTimestamp() == null) {
						//probably request detectedMovementInfo has not been received, so the snapshot does not exist
						cameraEnable = false;
					}
					if (!cameraEnable) {
						//sent email without picture
						EmailUtil.sendAlarmEmail(emailParameters);
					} else {
						String imageFileName = ExecuteShellComand.getFileName(appProperties.getSnapshotsPrefix(), 
								alarmInfoHolder.getLastDetectedMovementInfoTimestamp(), appProperties.getSnapshotsSuffix());
						//load file from the disk
						FileSystemResource file = new FileSystemResource(directory + "/" + imageFileName);
						try {
							EmailUtil.sendAlarmEmailWithAttachment(emailParameters, file);
						} catch (MessagingException e) {
							LOGGER.error("Could not send the email with picture.", e);
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("Could not send email.", e);
			}
		}
				
		//disable alarm after movement is detected
		disableAlarm();
		
		//start making snapshot pictures
		if (appProperties.isCameraEnable()) {
			snapshotPictures(appProperties.getSnapshotsInterval());
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
			//send email
			if (appProperties.isEmailEnable()) {
				try {
					EmailParameters emailParameters = new EmailParameters(appProperties.getEmailFrom(), 
							appProperties.getEmailFromPassword(), appProperties.getEmailTo());
					EmailUtil.sendAlarmEmail(emailParameters);
				} catch (Exception e) {
					LOGGER.error("Could not send email.", e);
				}
			}
			if (appProperties.isSmsEnable()) {
				//sends SMS
				try {
					SmsParameters smsParameters = new SmsParameters(appProperties.getSmsAccountSid(), 
							appProperties.getSmsAccountAuthToken(), appProperties.getPhoneNumberTo(), appProperties.getPhoneNumberFrom());
					SmsUtil.sendSms(smsParameters, "WARN: ESP batteries are low !" + currentVolts + " V.");
				} catch (Exception e) {
					LOGGER.error("Could not send SMS.", e);
				}			
			}
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
	public Object getSnapshotPictures(long snapshotInterval) {
		return doSnapshotPictures(snapshotInterval);
	}
	

	@Override
	@Async
	public void snapshotPictures(long snapshotInterval) {
		doSnapshotPictures(snapshotInterval);
	}
	
	private String doSnapshotPictures(long snapshotInterval) {
		ExecuteShellComand.stopMotion();
		long currTimestamp = System.currentTimeMillis();
		long deadlineTimestamp = currTimestamp + snapshotInterval;
		String snapshotsDir = appProperties.getSnapshotsDir();
		String fullSnapshotsDir = snapshotsDir + currTimestamp;
		String snapshotsPrefix = appProperties.getSnapshotsPrefix();
		String snapshotsSuffix = appProperties.getSnapshotsSuffix();
		ExecuteShellComand.createSnapshotDir(snapshotsDir, currTimestamp);
		LOGGER.info("Start snapshot to directory {}.", currTimestamp);
		while (System.currentTimeMillis() < deadlineTimestamp) {
			String fileName = ExecuteShellComand.getFileName(snapshotsPrefix, System.currentTimeMillis(), snapshotsSuffix);
			ExecuteShellComand.snapshotImage(fullSnapshotsDir, fileName);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		LOGGER.info("Stop snapshot to the directory {}.", currTimestamp);
		
		//ExecuteShellComand.startMotion();
		
		ExecuteShellComand.zipSnapshotDir(snapshotsDir, currTimestamp);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ExecuteShellComand.deleteSnapshotDir(snapshotsDir, currTimestamp);
		
		ExecuteShellComand.changeOwnershipSnapshotDir(snapshotsDir, currTimestamp);
		
		//TODO return the zip file
		return null;
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