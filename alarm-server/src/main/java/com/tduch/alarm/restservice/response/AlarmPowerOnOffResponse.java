package com.tduch.alarm.restservice.response;

public class AlarmPowerOnOffResponse {

	private final PowerState powerState;

	public AlarmPowerOnOffResponse(PowerState state) {
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

	public static enum PowerState {
		ON("ALARM_ON"), 
		OFF("ALARM_OFF");

		private final String state;
		
		private PowerState(String state) {
			this.state = state;
		}

		public String getState() {
			return state;
		}
		
	}
}
