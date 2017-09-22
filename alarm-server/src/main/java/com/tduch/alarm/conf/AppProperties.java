package com.tduch.alarm.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

	@Value("${app.email.from}")
	private String emailFrom;
	
	@Value("${app.email.from.password}")
	private String emailFromPassword;
	
	@Value("${app.email.to}")
	private String emailTo;
	
	@Value("${app.sms.account.sid}")
	private String smsAccountSid;
	
	@Value("${app.sms.account.auth.token}")
	private String smsAccountAuthToken;
	
	@Value("${app.phone.number.to}")
	private String phoneNumberTo;
	
	@Value("${app.phone.number.from}")
	private String phoneNumberFrom;
	
	

	public String getEmailFrom() {
		return emailFrom;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public String getEmailFromPassword() {
		return emailFromPassword;
	}

	public String getSmsAccountSid() {
		return smsAccountSid;
	}

	public String getSmsAccountAuthToken() {
		return smsAccountAuthToken;
	}

	public String getPhoneNumberTo() {
		return phoneNumberTo;
	}

	public String getPhoneNumberFrom() {
		return phoneNumberFrom;
	}

	
}
