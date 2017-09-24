package com.tduch.alarm.external;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecuteShellComand {

	public static void main(String[] args) {

		ExecuteShellComand obj = new ExecuteShellComand();
		String subject = "test subject 2";
		String emailTo = "duchalarm2@gmail.com";
		String content = "hello content";
		
		//String[] command = {"/bin/sh", "-c", "echo 'hello'|mail -s " + subject  + " " + emailTo};
		String[] command = {"/bin/sh", "-c", "echo '" + content + "'|mail -s '" + subject  + "' " + emailTo};

		String output = obj.executeCommand(command);

		System.out.println(output);

	}

	private String executeCommand(String[] command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader =
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}

}