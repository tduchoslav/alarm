package com.tduch.alarm.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.conf.EmailParameters;
import com.tduch.alarm.conf.SmsParameters;
import com.tduch.alarm.email.EmailUtil;
import com.tduch.alarm.entity.AlarmEmailInfoEntity;
import com.tduch.alarm.holder.AlarmInfoHolder;
import com.tduch.alarm.service.AlarmEmailInfoService;
import com.tduch.alarm.sms.SmsUtil;

@Service
public class DetectedMovementMonitor {

	private final static Logger LOGGER = LoggerFactory.getLogger(DetectedMovementMonitor.class);
	
	@Autowired
	AlarmInfoHolder alarmInfoHolder;
	
	@Autowired
	private AlarmEmailInfoService alarmEmailInfoService;
	
	@Autowired
	private AppProperties appProperties;
	
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
		try {
			SmsParameters smsParameters = new SmsParameters(appProperties.getSmsAccountSid(), 
					appProperties.getSmsAccountAuthToken(), appProperties.getPhoneNumberTo(), appProperties.getPhoneNumberFrom());
			if (appProperties.isSmsEnable()) {
				SmsUtil.sendSms(smsParameters, "WARNING: Alarm server has not received deactivation request!");
				LOGGER.info("Sending sms...");
			}
		} catch (Exception e) {
			LOGGER.error("Could not send SMS.", e);
		}
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
				AlarmEmailInfoEntity emailInfoEtity = new AlarmEmailInfoEntity();
				emailInfoEtity.setEmailInfo("movement detected but alarm has not been deactivated.");
				emailInfoEtity.setSentTmstmp(System.currentTimeMillis());
				alarmEmailInfoService.insert(emailInfoEtity);
				EmailUtil.sendAlarmEmail(emailParameters);	
				LOGGER.info("Sending email...");
			}
		} catch (Exception e) {
			LOGGER.error("Could not send email.", e);
		}
	}
}
