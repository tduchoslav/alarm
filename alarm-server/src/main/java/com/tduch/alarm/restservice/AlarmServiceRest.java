package com.tduch.alarm.restservice;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

//
//	@RequestMapping(value = "/alarmHeartBeat/{name}", method = RequestMethod.GET)
//	public HeartBeatResponse alarmHeartBeat(@RequestParam(value = "name") String name) {
//		alarmService.alarmHeartBeat();
//		return new HeartBeatResponse(counter.incrementAndGet(), String.format(template, name));
//	}
	
	//@RequestMapping("/alarmHeartBeatRest")
	@RequestMapping(value={"/alarmHeartBeatRest", "/heartBeat"})
	public HeartBeatResponse alarmHeartBeat() {
		alarmService.alarmHeartBeat();
		return new HeartBeatResponse(counter.incrementAndGet(), String.format(template, counter.get()));
	}
	
	//@RequestMapping("/alarmEnableRest")
	@RequestMapping(value={"/alarmEnableRest", "/enable"})
	public AlarmPowerOnOffResponse alarmEnable() {
		alarmService.enableAlarm();
		return new AlarmPowerOnOffResponse(AlarmPowerOnOffResponse.PowerState.ON);
	}
	
	//@RequestMapping("/alarmDisableRest")
	@RequestMapping(value={"/alarmDisableRest", "/disable"})
	public AlarmPowerOnOffResponse alarmDisable() {
		alarmService.disableAlarm();
		return new AlarmPowerOnOffResponse(AlarmPowerOnOffResponse.PowerState.OFF);
	}
	
	//@RequestMapping("/alarmMovementDetectedRest")
	@RequestMapping(value={"/alarmMovementDetectedRest", "/movement"})
	public AlarmMovementDetectedResponse alarmMovementDetected() {
		alarmService.detectedMovement();
		return new AlarmMovementDetectedResponse();
	}
	
	//@RequestMapping("/alarmMovementDetectedInfoRest")
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
	public String alarmInfoStatus() {
		AlarmStatusResponse alarmStatusResponse = null;
		if (alarmService.isAlarmEnabled()) {
			alarmStatusResponse = new AlarmStatusResponse(AlarmPowerOnOffResponse.PowerState.ON);
		} else {
			alarmStatusResponse = new AlarmStatusResponse(AlarmPowerOnOffResponse.PowerState.OFF);
		}
		return alarmStatusResponse.getPowerState().getState();
	}
	
	@RequestMapping(value={"/alarmTestRest", "/test"})
	//public TestResponse alarmTest() {
	public String alarmTest() {
		alarmService.test();
		TestResponse testResponse = new TestResponse("TEST_OK");
		return testResponse.getTest();
	}
	
	//voltage/currentVolts=6,5
	@RequestMapping(value={"/alarmBatteryVoltageStatusRest", "/voltage"} , method = RequestMethod.GET)
	public TestResponse alarmBatteryVoltageStatus(@RequestParam double currentVolts) {
		alarmService.processVoltage(currentVolts);
		return new TestResponse("test");
	}

	@RequestMapping(value = { "/alarmLogsRest", "/logs" })
	public String alarmLogs() {
		String logs = alarmService.getLogs();
		TestResponse testResponse = new TestResponse(logs);
		return testResponse.getTest();
	}
	
	@RequestMapping(value={"/alarmSnapshotsPictureRest", "/snapshots"})
	public String alarmSnapshots() {
		alarmService.snapshotPictures(appProperties.getSnapshotsInterval());
		TestResponse testResponse = new TestResponse("SNAPSHOTS_START");
		return testResponse.getTest();
	}

}
