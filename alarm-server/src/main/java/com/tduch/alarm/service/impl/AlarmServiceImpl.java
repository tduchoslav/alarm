package com.tduch.alarm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.conf.EmailParameters;
import com.tduch.alarm.conf.SmsParameters;
import com.tduch.alarm.email.EmailUtil;
import com.tduch.alarm.entity.AlarmEmailInfoEntity;
import com.tduch.alarm.holder.AlarmInfoHolder;
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
	private AppProperties appProperties;
	
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


	public void disableAlarm() {
		LOGGER.info("Alarm disabled.");
		alarmInfoHolder.clearHeartBeats();
		alarmStatusService.setAlarmStatus(false);
	}


	public void enableAlarm() {
		LOGGER.info("Alarm enabled.");
		alarmInfoHolder.clearHeartBeats();
		alarmStatusService.setAlarmStatus(true);
	}


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
					AlarmEmailInfoEntity emailInfoEtity = new AlarmEmailInfoEntity();
					emailInfoEtity.setEmailInfo("movement detected warning.");
					emailInfoEtity.setSentTmstmp(System.currentTimeMillis());
					alarmEmailInfoService.insert(emailInfoEtity);
					EmailUtil.sendAlarmEmail(emailParameters);
				}
			} catch (Exception e) {
				LOGGER.error("Could not send email.", e);
			}
		}
	}


	public boolean isAlarmEnabled() {
		return alarmStatusService.isAlarmStatusOn();
	}


	public boolean test() {
		LOGGER.info("Test if communication is connected.");
		return true;
	}


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

}