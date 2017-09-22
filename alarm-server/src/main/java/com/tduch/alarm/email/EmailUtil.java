package com.tduch.alarm.email;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static void sendWarningEmail(EmailParameters emailParameters) {
		LOGGER.info("The warning email is being sent.");
		sendEmail(emailParameters, "Alarm stop answering.", "Alarm is off. Probably battery is low, or the alarm is broken. Check that!");
	}
		

	private static void sendEmail(EmailParameters emailParameters, String subject, String content) {
		//TODO!!!!
		// Recipient's email ID needs to be mentioned.
	      final String to = emailParameters.getEmailTo();

	      // Sender's email ID needs to be mentioned
	      final String from = emailParameters.getEmailFrom();
	      
	      final String password = emailParameters.getEmailFromPassword(); // correct password for gmail id

	      // Assuming you are sending email from localhost
	      //String host = "localhost";
	      String host = "gmail.com";

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);
	      //properties.setProperty("mail.smtp.port", "465");
	      properties.setProperty("mail.smtp.port", "587");
	      properties.setProperty("mail.smtp.auth", "true"); //enable authentication
	      properties.setProperty("mail.smtp.starttls.enable", "true"); //enable STARTTLS
	      //properties.setProperty("mail.smtp.starttls.enable", "false"); //enable STARTTLS
	      properties.setProperty("mail.smtp.connectiontimeout", "10000");
	      properties.setProperty("mail.smtp.timeout", "10000");
	      
	      //create Authenticator object to pass in Session.getInstance argument
			Authenticator auth = new Authenticator() {
				//override the getPasswordAuthentication method
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(from, password);
				}
			};
			Session session = Session.getInstance(properties, auth);

			EmailUtil.sendEmail(session, to,"TLSEmail Testing Subject", "TLSEmail Testing Body");
			

//	      // Get the default Session object.
//	      Session session = Session.getDefaultInstance(properties);
//
//	      try {
//	         // Create a default MimeMessage object.
//	         MimeMessage message = new MimeMessage(session);
//
//	         // Set From: header field of the header.
//	         message.setFrom(new InternetAddress(from));
//
//	         // Set To: header field of the header.
//	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//
//	         // Set Subject: header field
//	         message.setSubject(subject);
//
//	         // Now set the actual message
//	         message.setText(content);
//
//	         // Send message
//	         Transport.send(message);
//	         System.out.println("Sent message successfully....");
//	      } catch (Exception mex) {
//	         mex.printStackTrace();
//	      }
	  }
	
	/**
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	public static void sendEmail(Session session, String toEmail, String subject, String body){
		try
	    {
	      MimeMessage msg = new MimeMessage(session);
	      //set message headers
	      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");

	      msg.setFrom(/*new InternetAddress("no_reply@journaldev.com", "NoReply-JD")*/);

	      //msg.setReplyTo(InternetAddress.parse("no_reply@journaldev.com", false));

	      msg.setSubject(subject, "UTF-8");

	      msg.setText(body, "UTF-8");

	      msg.setSentDate(new Date());

	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
	      System.out.println("Message is ready");
    	  //Transport.send(msg);  
    	  javax.mail.Transport transport = session.getTransport("smtp");
    	  transport.connect();
    	  transport.sendMessage(msg,msg.getAllRecipients());

	      System.out.println("EMail Sent Successfully!!");
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	}

}


