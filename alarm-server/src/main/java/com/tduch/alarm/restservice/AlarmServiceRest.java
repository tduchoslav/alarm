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

@RestController
public class AlarmServiceRest {

	@Autowired
	private AlarmService alarmService;
	
	@Autowired
	private AppProperties appProperties;
	
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
	
	@RequestMapping(value={"/alarmInfoStatusRest", "/status"})
	public AlarmStatusResponse alarmInfoStatus() {
		AlarmStatusResponse alarmStatusResponse = null;
		if (alarmService.isAlarmEnabled()) {
			alarmStatusResponse = new AlarmStatusResponse(AlarmPowerOnOffResponse.PowerState.ON);
		} else {
			alarmStatusResponse = new AlarmStatusResponse(AlarmPowerOnOffResponse.PowerState.OFF);
		}
		return alarmStatusResponse;
	}
	
	@RequestMapping(value={"/alarmTestRest", "/test"})
	public TestResponse alarmTest() {
		alarmService.test();
		TestResponse testResponse = new TestResponse("TEST_OK");
		return testResponse;
	}
	
//	//voltage/currentVolts=6,5
//	@RequestMapping(value={"/alarmBatteryVoltageStatusRest", "/voltage"} , method = RequestMethod.GET)
//	public TestResponse alarmBatteryVoltageStatus(@RequestParam double currentVolts) {
//		alarmService.processVoltage(currentVolts);
//		return new TestResponse("test");
//	}

	@RequestMapping(value = { "/alarmLogsRest", "/logs" })
	public TestResponse alarmLogs() {
		String logs = alarmService.getLogs();
		TestResponse testResponse = new TestResponse(logs);
		return testResponse;
	}
	
	@RequestMapping(value={"/alarmSnapshotsPictureRest", "/snapshots"})
	public String alarmSnapshots() {
		alarmService.snapshotPictures(appProperties.getSnapshotsInterval());
		TestResponse testResponse = new TestResponse("SNAPSHOTS_START");
		return testResponse.getTest();
	}
	
	/**
	 * TODO return zip file with the pictures from the snapshot!!!
	 * @return
	 */
	@RequestMapping(value={"/alarmGetSnapshotsPictureRest", "/getSnapshots"})
	public Object alarmGetSnapshots() {
		return alarmService.getSnapshotPictures(appProperties.getSnapshotsInterval());
	}
	
	@RequestMapping(value={"/startCameraMotionRest", "/startMotion"})
	public void startMotion() {
		alarmService.startMotionCamera();
	}
	
	@RequestMapping(value={"/stopCameraMotionRest", "/stoptMotion"})
	public void stopMotion() {
		alarmService.stopMotionCamera();
	}
	

}
