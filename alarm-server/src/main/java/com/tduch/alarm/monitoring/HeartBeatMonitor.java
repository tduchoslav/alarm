package com.tduch.alarm.monitoring;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tduch.alarm.email.EmailSenderData;
import com.tduch.alarm.email.EmailSenderService;
import com.tduch.alarm.holder.AlarmInfoHolder;
import com.tduch.alarm.service.AlarmService;
import com.tduch.alarm.sms.SmsSenderData;
import com.tduch.alarm.sms.SmsSenderService;
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
	private SmsSenderService smsSenderService;
	@Autowired
	private EmailSenderService emailSenderService;
	
	
	@Scheduled(fixedDelay = FIXED_INTERVAL, initialDelay = 1800000)
	public void scheduleAlarmCheck() {
		LOGGER.info("Monitoring of the heart beats started.");
		
		
		if (!alarmService.isAlarmEnabled()) {
			LOGGER.info("Alarm is switched off, do not check anything.");
		} else {
			if ((System.currentTimeMillis() - FIXED_INTERVAL) > alarmInfoHolder.getLastHeartBeatTimestamp()) {
				LOGGER.info("Last heartbeat noticed at {}. Probably the alarm is dead.", alarmInfoHolder.getLastHeartBeatTimestamp());
				//trigger message, but max. 3 times, then switch off the alarm
				if (alarmInfoHolder.getSentCount() <= 3) {
					alarmInfoHolder.addCount();
					
					SmsSenderData smsData = new SmsSenderData("WARNING: Alarm server has not received heart beat! Last received at " + new Date(alarmInfoHolder.getLastHeartBeatTimestamp()));
					smsSenderService.send(smsData);
					EmailSenderData emailData = new EmailSenderData("Heart beat warning.", true);
					emailSenderService.send(emailData);
					
				} else {
					alarmService.disableAlarm();
				}
			} else {
				LOGGER.debug("Everything is ok, the server received the heartbeat within the last interval {} sec.", FIXED_INTERVAL);
			}
		}
	}
}
