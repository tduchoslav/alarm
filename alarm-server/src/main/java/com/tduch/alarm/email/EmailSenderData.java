package com.tduch.alarm.email;

import com.tduch.alarm.send.SenderData;

public class EmailSenderData extends SenderData {

	private boolean isWarningOnly = false;
	
	public EmailSenderData(String message) {
		super(message);
	}

	
	public EmailSenderData(String message, boolean isWarningOnly) {
		super(message);
		this.isWarningOnly = isWarningOnly;
	}

	public boolean isWarningOnly() {
		return isWarningOnly;
	}
}
