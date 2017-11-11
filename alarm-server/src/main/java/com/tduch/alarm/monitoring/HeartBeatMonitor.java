package com.tduch.alarm.monitoring;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.conf.EmailParameters;
import com.tduch.alarm.conf.SmsParameters;
import com.tduch.alarm.email.EmailUtil;
import com.tduch.alarm.entity.AlarmEmailInfoEntity;
import com.tduch.alarm.holder.AlarmInfoHolder;
import com.tduch.alarm.service.AlarmEmailInfoService;
import com.tduch.alarm.service.AlarmService;
import com.tduch.alarm.sms.SmsUtil;
/**
 * Monitoring of the alarm heart beats, every several minutes the server checks if the alarm sends the heart beats.
 * If the heart beats are not coming during specified interval, the server sends warning message.
 * @author tomas
 *
 */
@Service
public class HeartBeatMonitor {

	private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatMonitor.class);

	private final static int FIXED_INTERVAL = 7200000; // 2 hour.
	
	@Autowired
	private AlarmInfoHolder alarmInfoHolder;
	
	@Autowired
	private AlarmService alarmService;
	
	@Autowired
	private AlarmEmailInfoService alarmEmailInfoService;
	
	@Autowired
	private AppProperties appProperties;
	
	@Scheduled(fixedDelay = FIXED_INTERVAL)
	public void scheduleAlarmCheck() {
		LOGGER.info("Monitoring of the heart beats started.");
		if (!alarmService.isAlarmEnabled()) {
			LOGGER.info("Alarm is switched off, do not check anything.");
		} else {
			if ((System.currentTimeMillis() - FIXED_INTERVAL) > alarmInfoHolder.getLastHeartBeatTimestamp()) {
				LOGGER.warn("Last heartbeat noticed at {}. Probably the alarm is dead.", alarmInfoHolder.getLastHeartBeatTimestamp());
				//trigger message, but max. 3 times, then switch off the alarm
				if (alarmInfoHolder.getSentCount() <= 3) {
					alarmInfoHolder.addCount();
					try {
						SmsParameters smsParameters = new SmsParameters(appProperties.getSmsAccountSid(), 
								appProperties.getSmsAccountAuthToken(), appProperties.getPhoneNumberTo(), appProperties.getPhoneNumberFrom());
						if (appProperties.isSmsEnable()) {
							SmsUtil.sendSms(smsParameters, "WARNING: Alarm server has not received heart beat! Last received at " + new Date(alarmInfoHolder.getLastHeartBeatTimestamp()));
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
							if (alarmInfoHolder.getSentCount() >= 1) { 
								EmailUtil.sendWarningEmailNoSms(emailParameters, alarmInfoHolder.getLastHeartBeatTimestamp());
							} else {
								AlarmEmailInfoEntity emailInfoEtity = new AlarmEmailInfoEntity();
								emailInfoEtity.setEmailInfo("heart beat warning");
								emailInfoEtity.setSentTmstmp(System.currentTimeMillis());
								alarmEmailInfoService.insert(emailInfoEtity);
								EmailUtil.sendWarningEmail(emailParameters, alarmInfoHolder.getLastHeartBeatTimestamp());	
							}	
							LOGGER.info("Sending email...");
						}
					} catch (Exception e) {
						LOGGER.error("Could not send email.", e);
					}
				} else {
					alarmService.disableAlarm();
				}
			} else {
				LOGGER.debug("Everything is ok, the server received the heartbeat within the last interval {} sec.", FIXED_INTERVAL);
			}
		}
	}
}
