package com.tduch.alarm.spot;
/**
 * Enum with all spots where the alarm could be placed
 * @author tomas
 *
 */
public enum Spot {

	DOOR(1, "door"),
	LIVING_ROOM(2, "livingroom"),
	CHILD_BEDROOM(3, "childbedroom"),
	ADULT_BEDROOM(4, "parentbedroom");

	private final int id;
	private final String name;
	
	private Spot(int id, String name){
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * Returns null if no spot could be found by given name
	 * @param name
	 * @return
	 */
	public static Spot findByName(String name) {
		for (Spot val : values()) {
			if (name.equals(val.getName())) {
				return val;
			}
		}
		return null;
	}
	
}
