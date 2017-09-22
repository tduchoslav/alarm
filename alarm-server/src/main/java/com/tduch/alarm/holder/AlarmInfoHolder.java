package com.tduch.alarm.holder;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Singleton keeps alarm last heart beat pings.
 * @author tomas
 *
 */
public final class AlarmInfoHolder {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AlarmInfoHolder.class);
	
	private long lastHeartBeatTimestamp;
	
	private List<Long> allHeartBeatTimestamps =  new LinkedList<Long>();
	
	//private boolean isAlarmOn = false;
	
	private int sentCount = 0;
	
//	@PostConstruct
//    public void infoPostConstruct() {
//         System.out.println("I am initalized!" + this);
//    }
	
	public AlarmInfoHolder() {
		
	}


	public long getLastHeartBeatTimestamp() {
		return lastHeartBeatTimestamp;
	}


	public void setLastHeartBeatTimestamp(long lastHeartBeatTimestamp) {
		this.lastHeartBeatTimestamp = lastHeartBeatTimestamp;
		allHeartBeatTimestamps.add(lastHeartBeatTimestamp);
	}


//	public boolean isAlarmOn() {
//		return isAlarmOn;
//	}


	public void clearHeartBeats(/*boolean isAlarmOn*/) {
		//this.isAlarmOn = isAlarmOn;
		allHeartBeatTimestamps.clear();
		lastHeartBeatTimestamp = System.currentTimeMillis();
		sentCount = 0;
	}


	public int getSentCount() {
		return sentCount;
	}


	public void setSentCount(int sentCount) {
		this.sentCount = sentCount;
	}
	
	public void addCount() {
		this.sentCount++;
	}
	
	/**
	 * 
	 * If the alarm server is restarted (e.g. by power outage, etc. ) 
	 * we can decide according to the heart beats if to enable alarm again.
	 * If the the server receives 3 heart beats in 1 hour we can say the alarm could be be enabled
	 */
	public boolean checkIfEnableAlarm() {
		LOGGER.debug("last heartBeats: " + allHeartBeatTimestamps.toString());
		if (allHeartBeatTimestamps.size() > 3) {
			Long heartBeatTimestamp = allHeartBeatTimestamps.get((allHeartBeatTimestamps.size() - 4));
			if (heartBeatTimestamp > (System.currentTimeMillis() - 3600000)) {
				return true;
			}
		}
		return false;
	}
	
}
