package com.tduch.alarm.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tduch.alarm.datasource.DatasourceType;

@Component
public class AppProperties {

	@Value("${app.email.enable}")
	private boolean emailEnable;
	
	@Value("${app.email.from.1}")
	private String emailFrom;
	
	@Value("${app.email.from.password.1}")
	private String emailFromPassword;
	
	@Value("${app.email.to.1}")
	private String emailTo;
	
	@Value("${app.email.from.2}")
	private String emailFrom2;
	
	@Value("${app.email.from.password.2}")
	private String emailFromPassword2;
	
	@Value("${app.email.to.2}")
	private String emailTo2;
	
	@Value("${app.email.max.per.month}")
	private Integer maxNumEmailsPerMonth;
	
	@Value("${app.sms.enable}")
	private boolean smsEnable;
	
	@Value("${app.sms.account.sid}")
	private String smsAccountSid;
	
	@Value("${app.sms.account.auth.token}")
	private String smsAccountAuthToken;
	
	@Value("${app.phone.number.to}")
	private String phoneNumberTo;
	
	@Value("${app.phone.number.from}")
	private String phoneNumberFrom;
	
	@Value("${app.datasource.type}")
	private String datasourceType;
	

	public String getEmailFrom() {
		return emailFrom;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public String getEmailFromPassword() {
		return emailFromPassword;
	}

	public String getSmsAccountSid() {
		return smsAccountSid;
	}

	public String getSmsAccountAuthToken() {
		return smsAccountAuthToken;
	}

	public String getPhoneNumberTo() {
		return phoneNumberTo;
	}

	public String getPhoneNumberFrom() {
		return phoneNumberFrom;
	}

	public String getEmailFrom2() {
		return emailFrom2;
	}

	public String getEmailFromPassword2() {
		return emailFromPassword2;
	}

	public String getEmailTo2() {
		return emailTo2;
	}

	public boolean isEmailEnable() {
		return emailEnable;
	}

	public boolean isSmsEnable() {
		return smsEnable;
	}

	public Integer getMaxNumEmailsPerMonth() {
		//if no limit is defined in property file, return unlimited value
		if (maxNumEmailsPerMonth == null || maxNumEmailsPerMonth == 0) {
			return 99999999;
		}
		return maxNumEmailsPerMonth;
	}

	
	public String getDatasourceType() {
		return datasourceType;
	}

	public boolean isMongoDatasource() {
		return (DatasourceType.MONGODB == DatasourceType.findByName(getDatasourceType())) ? true : false;
	}

}
