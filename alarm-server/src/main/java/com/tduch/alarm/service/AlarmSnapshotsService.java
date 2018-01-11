package com.tduch.alarm.service;

public interface AlarmSnapshotsService {

	/**
	 * Starts making snapshots from the web camera, for the given interval
	 */
	void snapshotPictures(long snapshotInterval);
	
	/**
	 * Starts making snapshots from the web camera, for the given interval and wait until the snapshot is done and returns the zip file.
	 */
	Object getSnapshotPictures(long snapshotInterval);
	
	
}
