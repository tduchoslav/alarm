package com.tduch.alarm.email;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.tduch.alarm.conf.EmailParameters;
import com.tduch.alarm.service.impl.AlarmServiceImpl;


public final class EmailUtil {
	
	private EmailUtil(){
	}
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AlarmServiceImpl.class);

	public static void main(String[] args) {
		System.out.println("main block");
		//TODO create emailParameters
//		EmailParameters emailParameters = new EmailParameters(emailFrom, emailFromPassword, emailTo);
//		sendAlarmEmail(emailParameters);
		System.out.println("email sending end");
	}
	/**
	 * Sends email when the alarm detected movement.
	 */
	public static void sendAlarmEmail(EmailParameters emailParameters) {
		sendEmail(emailParameters, "Alarm detected movement at your house!", "Alarm detected movement at your house!");
	}
	
	/**
	 * Sends email when the alarm stops pinging the server. Probably battery is off, or the alarm is broken.
	 */
	public static void sendWarningEmail(EmailParameters emailParameters, long lastHeartBeatTimestamp) {
		LOGGER.info("The warning email is being sent.");
		sendEmail(emailParameters, "Alarm stopped transmitting.",
				"Alarm is off. Probably heartbeat not received, or the alarm is broken. Check that! Last heartbeat timestamp receiced at:"
						+ lastHeartBeatTimestamp);
	}
	
	/**
	 * Sends email when the alarm stops pinging the server. Probably battery is off, or the alarm is broken.
	 */
	public static void sendWarningEmailNoSms(EmailParameters emailParameters, long lastHeartBeatTimestamp) {
		LOGGER.info("The warning email is being sent.");
		sendEmail(emailParameters, "Alarm stopped transmitting - no sms.",
				"Alarm is off. Probably heartbeat not received, or the alarm is broken. Check that! Last heartbeat timestamp receiced at:"
						+ lastHeartBeatTimestamp);
	}
		

	private static void sendEmail(EmailParameters emailParameters, String subject, String content) {
		LOGGER.debug("sent email to: {}, subject: {}, content: {}", emailParameters, subject, content);
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost("smtp.gmail.com");
	    mailSender.setPort(587);
	    mailSender.setUsername(emailParameters.getEmailFrom());
	    mailSender.setPassword(emailParameters.getEmailFromPassword());
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
	    
	    SimpleMailMessage message = new SimpleMailMessage(); 
        message.setTo(emailParameters.getEmailTo()); 
        message.setSubject(subject); 
        message.setText(content);
        mailSender.send(message);
	  }

}


