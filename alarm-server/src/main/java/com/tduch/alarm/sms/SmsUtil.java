package com.tduch.alarm.sms;

import com.tduch.alarm.conf.SmsParameters;
import com.twilio.sdk.Twilio;
import com.twilio.sdk.creator.api.v2010.account.MessageCreator;
import com.twilio.sdk.resource.api.v2010.account.Message;
import com.twilio.sdk.type.PhoneNumber;


/**
 * Utility for sending SMS
 * 
 * @author tomas
 *
 */
public class SmsUtil {
	// Find your Account Sid and Token at twilio.com/user/account

	public static void sendSms(SmsParameters smsParameters, String message) {

		Twilio.init(smsParameters.getSmsAccountSid(), smsParameters.getSmsAccountAuthToken());
		
		MessageCreator creator = Message.create(smsParameters.getSmsAccountSid(), new PhoneNumber(smsParameters.getPhoneNumberTo()),
				new PhoneNumber(smsParameters.getPhoneNumberFrom()), message);
		creator.execute();
	}
	
	public static void main(String[] args) {
		System.out.println("main block");
		//TODO for testing purposes
//		SmsParameters smsParameters = new SmsParameters(smsAccountSid, smsAccountAuthToken, phoneNumberTo, phoneNumberFrom);
//		sendSms(smsParameters, "testing");
		System.out.println("sms sending end");
	}
}
