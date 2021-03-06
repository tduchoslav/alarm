package com.tduch.alarm.restservice;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tduch.alarm.conf.AppProperties;
import com.tduch.alarm.restservice.response.AlarmMovementDetectedResponse;
import com.tduch.alarm.restservice.response.AlarmPowerOnOffResponse;
import com.tduch.alarm.restservice.response.AlarmStatusResponse;
import com.tduch.alarm.restservice.response.HeartBeatResponse;
import com.tduch.alarm.restservice.response.TestResponse;
import com.tduch.alarm.service.AlarmService;
import com.tduch.alarm.service.AlarmSnapshotsService;

@RestController
public class AlarmServiceRest {

	@Autowired
	private AlarmService alarmService;
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private AlarmSnapshotsService alarmSnapshotsService;
	
	private static final String template = "HeartBeat count %s received.";
	private final AtomicLong counter = new AtomicLong();
	
	@RequestMapping(value={"/alarmHeartBeatRest", "/heartBeat"})
	public HeartBeatResponse alarmHeartBeat() {
		alarmService.alarmHeartBeat();
		return new HeartBeatResponse(counter.incrementAndGet(), String.format(template, counter.get()));
	}
	
	@RequestMapping(value={"/alarmEnableRest", "/enable"})
	public AlarmPowerOnOffResponse alarmEnable() {
		alarmService.enableAlarm();
		return new AlarmPowerOnOffResponse(AlarmPowerOnOffResponse.PowerState.ON);
	}
	
	@RequestMapping(value={"/alarmDisableRest", "/disable"})
	public AlarmPowerOnOffResponse alarmDisable() {
		alarmService.disableAlarm();
		return new AlarmPowerOnOffResponse(AlarmPowerOnOffResponse.PowerState.OFF);
	}
	
	@RequestMapping(value={"/alarmMovementDetectedRest", "/movement"})
	public AlarmMovementDetectedResponse alarmMovementDetected() {
		alarmService.detectedMovement();
		return new AlarmMovementDetectedResponse();
	}
	
	@RequestMapping(value={"/alarmMovementDetectedInfoRest", "/movementInfo"})
	public AlarmMovementDetectedResponse alarmMovementDetectedInfo() {
		/*
		 * alarm HW detects movement and immediately sends request to the server
		 * when the server receives this request and then no other request comes it means, that something happen
		 * and must send warning to via sms
		 */
		alarmService.detectedMovementInfo();
		return new AlarmMovementDetectedResponse();
	}
	
	@RequestMapping(value={"/alarmInfoStatusRest", "/statusRest"})
	public AlarmStatusResponse alarmInfoStatus() {
		AlarmStatusResponse alarmStatusResponse = null;
		if (alarmService.isAlarmEnabled()) {
			alarmStatusResponse = new AlarmStatusResponse(AlarmPowerOnOffResponse.PowerState.ON);
		} else {
			alarmStatusResponse = new AlarmStatusResponse(AlarmPowerOnOffResponse.PowerState.OFF);
		}
		return alarmStatusResponse;
	}
	
	/**
	 * Special test just for arduino which can process just string value
	 */
	@RequestMapping(value={"/alarmInfoStatusRest", "/status"})
	public String alarmInfoStatusString() {
		AlarmStatusResponse alarmInfoStatus = alarmInfoStatus();
		return alarmInfoStatus.getPowerState().getState();
	}
	
	
	@RequestMapping(value={"/alarmTestRest", "/test"})
	public TestResponse alarmTest() {
		alarmService.test();
		TestResponse testResponse = new TestResponse("TEST_OK");
		return testResponse;
	}
	

	@RequestMapping(value = { "/alarmLogsRest", "/logs" })
	public TestResponse alarmLogs() {
		String logs = alarmService.getLogs();
		TestResponse testResponse = new TestResponse(logs);
		return testResponse;
	}
	
	@RequestMapping(value={"/alarmSnapshotsPictureRest", "/snapshots"})
	public String alarmSnapshots() {
		alarmSnapshotsService.snapshotPictures(appProperties.getSnapshotsInterval());
		TestResponse testResponse = new TestResponse("SNAPSHOTS_START");
		return testResponse.getTest();
	}
	
	/**
	 * TODO return zip file with the pictures from the snapshot!!!
	 * @return
	 */
	@RequestMapping(value={"/alarmGetSnapshotsPictureRest", "/getSnapshots"})
	public Object alarmGetSnapshots() {
		return alarmSnapshotsService.getSnapshotPictures(appProperties.getSnapshotsInterval());
	}
	
	@RequestMapping(value={"/startCameraMotionRest", "/startMotion"})
	public void startMotion() {
		alarmService.startMotionCamera();
		//TODO return OK
	}
	
	@RequestMapping(value={"/stopCameraMotionRest", "/stopMotion"})
	public void stopMotion() {
		alarmService.stopMotionCamera();
		//TODO return OK
	}
	

}
