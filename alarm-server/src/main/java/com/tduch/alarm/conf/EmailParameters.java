package com.tduch.alarm.conf;

public class EmailParameters {

	private final String emailFrom;
	
	private final String emailFromPassword;
	
	private final String emailTo;

	public EmailParameters(String emailFrom, String emailFromPassword, String emailTo) {
		this.emailFrom = emailFrom;
		this.emailFromPassword = emailFromPassword;
		this.emailTo = emailTo;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public String getEmailFromPassword() {
		return emailFromPassword;
	}

	public String getEmailTo() {
		return emailTo;
	}
	
	
	
}
