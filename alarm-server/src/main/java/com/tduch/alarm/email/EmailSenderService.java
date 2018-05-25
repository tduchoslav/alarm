package com.tduch.alarm.email;

import com.tduch.alarm.conf.EmailParameters;
import com.tduch.alarm.send.SenderService;

public interface EmailSenderService extends SenderService<EmailSenderData> {

	EmailParameters getEmailParameters();
	
	void processSend();
	
	void processSendWarningOnly();
}
