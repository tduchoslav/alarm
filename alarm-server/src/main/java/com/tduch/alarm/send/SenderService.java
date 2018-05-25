package com.tduch.alarm.send;
/**
 * 
 * Sender handler service
 * Sends alarm info to implemented communication channels
 *
 */
public interface SenderService<T extends SenderData> {

	/**
	 * Sends given data
	 */
	void send(T data);
}
