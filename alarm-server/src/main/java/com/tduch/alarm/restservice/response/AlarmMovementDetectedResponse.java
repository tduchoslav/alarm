package com.tduch.alarm.restservice.response;

public class AlarmMovementDetectedResponse {
	private final long timestamp;
	
	public AlarmMovementDetectedResponse() {
		timestamp = System.currentTimeMillis();
	}

	public long getTimestamp() {
		return timestamp;
	}
	
}
