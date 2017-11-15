package com.tduch.alarm.datasource;

public enum DatasourceType {

	MONGODB("mongodb"), 
	HSQL("hsql");
	
	private final String name;
	
	private DatasourceType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns datasource type by given name. Return HSQL if could no be found any
	 * corresponding db.
	 */
	public static DatasourceType findByName(String name) {
		for (DatasourceType val : values()) {
			if (name.equals(val.getName())) {
				return val;
			}
		}
		return HSQL;
	}

}
