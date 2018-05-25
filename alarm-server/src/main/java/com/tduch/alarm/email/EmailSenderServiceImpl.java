package com.tduch.alarm.email;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.conf.EmailParameters;
import com.tduch.alarm.dto.AlarmEmailInfoDto;
import com.tduch.alarm.external.ExecuteShellComand;
import com.tduch.alarm.holder.AlarmInfoHolder;
import com.tduch.alarm.service.AlarmEmailInfoService;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

	private final static Logger LOGGER = LoggerFactory.getLogger(EmailSenderServiceImpl.class);

	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private AlarmInfoHolder alarmInfoHolder;
	
	@Autowired
	private AlarmEmailInfoService alarmEmailInfoService;

	@Override
	public void send(EmailSenderData data) {
		if (appProperties.isEmailEnable()) {
			LOGGER.info("Sending email...");
			try {
				AlarmEmailInfoDto emailInfoDto = new AlarmEmailInfoDto();
				emailInfoDto.setEmailInfo(data.getMessage());
				emailInfoDto.setSentTmstmp(System.currentTimeMillis());
				alarmEmailInfoService.insert(emailInfoDto);
				if (data.isWarningOnly()) {
					processSendWarningOnly();
				} else {
					processSend();
				}
			} catch (Exception e) {
				LOGGER.error("Could not send email.", e);
			}
		} else {
			LOGGER.debug("Email sending is disabled.");
		}
	}

	@Override
	public EmailParameters getEmailParameters() {
		EmailParameters emailParameters = null;
		if (alarmEmailInfoService.getSentEmailsCountInCurrentMonth() < appProperties.getMaxNumEmailsPerMonth()) {
			emailParameters = new EmailParameters(appProperties.getEmailFrom(), 
				appProperties.getEmailFromPassword(), appProperties.getEmailTo());
		} else {
			//max limit exceeded, send email to backup email
			emailParameters = new EmailParameters(appProperties.getEmailFrom2(), 
					appProperties.getEmailFromPassword2(), appProperties.getEmailTo2());
		}
		return emailParameters;
	}

	@Override
	public void processSend() {
		EmailParameters emailParameters = getEmailParameters();
		if (!appProperties.isCameraEnable()) {
			EmailUtil.sendAlarmEmail(emailParameters);
		} else {
			String directory = appProperties.getSnapshotsDir();
			String imageFileName = ExecuteShellComand.getFileName(appProperties.getSnapshotsPrefix(), 
					alarmInfoHolder.getLastDetectedMovementInfoTimestamp(), appProperties.getSnapshotsSuffix());
			//load file from the disk
			FileSystemResource file = new FileSystemResource(directory + "/" + imageFileName);
			try {
				EmailUtil.sendAlarmEmailWithAttachment(emailParameters, file);
			} catch (MessagingException e) {
				LOGGER.error("Could not send the email with picture.", e);
			}
			//delete image file
			String filePath = file.getPath();
			boolean deleted = file.getFile().delete();
			if (deleted) {
				LOGGER.info("The file {} has been deleted", filePath);
			} else {
				LOGGER.warn("The file {} could not been deleted", filePath);
			}
		}
	}


	@Override
	public void processSendWarningOnly() {
		EmailParameters emailParameters = getEmailParameters();
		if (alarmInfoHolder.getSentCount() >= 1) { 
			EmailUtil.sendWarningEmailNoSms(emailParameters, alarmInfoHolder.getLastHeartBeatTimestamp());
		} else {
			EmailUtil.sendWarningEmail(emailParameters, alarmInfoHolder.getLastHeartBeatTimestamp());	
		}	
	}

}
