package com.tduch.alarm.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.conf.SmsParameters;

@Service
public class SmsSenderServiceImpl implements SmsSenderService {

	private final static Logger LOGGER = LoggerFactory.getLogger(SmsSenderServiceImpl.class);

	@Autowired
	private AppProperties appProperties;
	
	/**
	 * Sends sms
	 */
	@Override
	public void send(SmsSenderData data) {
		if (appProperties.isSmsEnable()) {
			try {
				LOGGER.debug("send sms...");
				SmsParameters smsParameters = new SmsParameters(appProperties.getSmsAccountSid(), 
						appProperties.getSmsAccountAuthToken(), appProperties.getPhoneNumberTo(), appProperties.getPhoneNumberFrom());
				SmsUtil.sendSms(smsParameters, data.getMessage());
			} catch (Exception e) {
				LOGGER.error("Could not send SMS.", e);
			}			
		} else {
			LOGGER.debug("Sms sending is disabled.");
		}
	}

}
