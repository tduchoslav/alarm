package com.tduch.alarm.restservice.response;

public class TestResponse {
	private final String test;
	
	public TestResponse(String test) {
		this.test = test;
	}

	public String getTest() {
		return test;
	}
	
	public String toString() {
		return "TEST_OK";
	}
	
	
}
