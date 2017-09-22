package com.tduch.alarm.restservice.response;

import com.tduch.alarm.restservice.response.AlarmPowerOnOffResponse.PowerState;

public class AlarmStatusResponse {
	private final PowerState powerState;
	
	public AlarmStatusResponse(PowerState state) {
		powerState = state;
	}

	public PowerState getPowerState() {
		return powerState;
	}
	
	public boolean isEnabled() {
		if (powerState == PowerState.ON) {
			return true;
		}
		return false;
	}

	public String toString() {
		return powerState.getState();
	}
}
