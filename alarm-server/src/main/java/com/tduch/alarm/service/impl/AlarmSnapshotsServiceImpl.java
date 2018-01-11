package com.tduch.alarm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.external.ExecuteShellComand;
import com.tduch.alarm.service.AlarmSnapshotsService;

@Service
public class AlarmSnapshotsServiceImpl implements AlarmSnapshotsService {

	private final static Logger LOGGER = LoggerFactory.getLogger(AlarmSnapshotsServiceImpl.class);
	
	@Autowired
	private AppProperties appProperties;
	
	@Override
	public Object getSnapshotPictures(long snapshotInterval) {
		return doSnapshotPictures(snapshotInterval);
	}
	

	@Override
	@Async
	public void snapshotPictures(long snapshotInterval) {
		doSnapshotPictures(snapshotInterval);
	}
	
	private String doSnapshotPictures(long snapshotInterval) {
		ExecuteShellComand.stopMotion();
		long currTimestamp = System.currentTimeMillis();
		long deadlineTimestamp = currTimestamp + snapshotInterval;
		String snapshotsDir = appProperties.getSnapshotsDir();
		String fullSnapshotsDir = snapshotsDir + currTimestamp;
		String snapshotsPrefix = appProperties.getSnapshotsPrefix();
		String snapshotsSuffix = appProperties.getSnapshotsSuffix();
		ExecuteShellComand.createSnapshotDir(snapshotsDir, currTimestamp);
		LOGGER.info("Start snapshot to directory {}.", currTimestamp);
		while (System.currentTimeMillis() < deadlineTimestamp) {
			String fileName = ExecuteShellComand.getFileName(snapshotsPrefix, System.currentTimeMillis(), snapshotsSuffix);
			ExecuteShellComand.snapshotImage(fullSnapshotsDir, fileName);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		LOGGER.info("Stop snapshot to the directory {}.", currTimestamp);
		
		//ExecuteShellComand.startMotion();
		
		ExecuteShellComand.zipSnapshotDir(snapshotsDir, currTimestamp);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ExecuteShellComand.deleteSnapshotDir(snapshotsDir, currTimestamp);
		
		ExecuteShellComand.changeOwnershipSnapshotDir(snapshotsDir, currTimestamp);
		
		//TODO return the zip file
		return null;
	}

}
