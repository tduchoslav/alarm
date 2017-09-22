package com.tduch.alarm.conf;

public class SmsParameters {

	private final String smsAccountSid;
	
	private final String smsAccountAuthToken;
	
	private final String phoneNumberTo;
	
	private final String phoneNumberFrom;

	public SmsParameters(String smsAccountSid, String smsAccountAuthToken, String phoneNumberTo,
			String phoneNumberFrom) {
		super();
		this.smsAccountSid = smsAccountSid;
		this.smsAccountAuthToken = smsAccountAuthToken;
		this.phoneNumberTo = phoneNumberTo;
		this.phoneNumberFrom = phoneNumberFrom;
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
