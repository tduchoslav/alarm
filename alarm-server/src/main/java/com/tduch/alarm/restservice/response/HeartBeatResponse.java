package com.tduch.alarm.restservice.response;

public class HeartBeatResponse {

	private long id;
	
	private String content;
	
	public HeartBeatResponse(long incrementAndGet, String content) {
		id = incrementAndGet;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

}
