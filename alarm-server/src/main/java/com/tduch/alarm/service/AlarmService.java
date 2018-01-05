package com.tduch.alarm.service;
/**
 * Defines all public methods for alarm
 * @author tomas
 *
 */
public interface AlarmService {

	/**
	 * Receives ping from the alarm.
	 * if the alarm does not ping the server for defined interval, it will trigger the respective email.
	 */
	void alarmHeartBeat();
	
	/**
	 * Disables the alarm, so the server stop checking the heartbeat and other functionalities.
	 */
	void disableAlarm();
	
	/**
	 * Enables the alarm, so the server start checking the heartbeats and other functionalities.
	 */
	void enableAlarm();
	
	/**
	 * The movement has been detected while the alarm is enabled.
	 * 
	 */
	void detectedMovement();
	
	/**
	 * Returns true if the alarm is enabled.
	 * It allows to get status of the arduino alarm client.
	 * @return
	 */
	boolean isAlarmEnabled();
	
	/**
	 * Just for testing purposes.
	 * @return
	 */
	boolean test();

	/**
	 * Receives and processes volts from the alarm's batteries.
	 * @param currentVolts
	 */
	void processVoltage(double currentVolts);

	/**
	 * Receives info about movement from the alarm arduino.
	 */
	void detectedMovementInfo();

	/**
	 * Returns last logging info from the server
	 * 
	 * @return
	 */
	String getLogs();

	/**
	 * Starts making snapshots from the web camera, for the given interval
	 */
	void snapshotPictures(long snapshotInterval);
	
	/**
	 * Starts making snapshots from the web camera, for the given interval and wait until the snapshot is done and returns the zip file.
	 */
	Object getSnapshotPictures(long snapshotInterval);

	
	/**
	 * Stops motion dameon on the raspberry
	 */
	void stopMotionCamera();
	
	/**
	 * Starts motion dameon on the raspberry, so the camera gives live stream
	 */
	void startMotionCamera();
	
}