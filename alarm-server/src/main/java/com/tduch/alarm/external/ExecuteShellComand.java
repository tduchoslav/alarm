package com.tduch.alarm.external;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteShellComand {

	private final static Logger LOGGER = LoggerFactory.getLogger(ExecuteShellComand.class);
	
	private static ExecuteShellComand OBJ_EXE_SHELL = new ExecuteShellComand();
	
	
	public static void main(String[] args) {

//		ExecuteShellComand obj = new ExecuteShellComand();
		String subject = "test subject 2";
		String emailTo = "duchalarm2@gmail.com";
		String content = "hello content";
		
		//String[] command = {"/bin/sh", "-c", "echo 'hello'|mail -s " + subject  + " " + emailTo};
		String[] command = {"/bin/sh", "-c", "echo '" + content + "'|mail -s '" + subject  + "' " + emailTo};
		

		String output = OBJ_EXE_SHELL.executeCommand(command[2]);

		System.out.println(output);

	}

	private String executeCommand(String command) {
		LOGGER.info("Run command: {}", command);
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
			LOGGER.error("Could not execute the shell command. ", e);
			//e.printStackTrace();
		}

		return output.toString();

	}
	
	/**
	 * Snpashots image via fswebcam to the given directory
	 */
	public static void snapshotImage(String directory, String pictureName) {
		String command = String.format("fswebcam -S 20 --fps 15 -r 640x480 %s/%s" , directory, pictureName);
		String executeCommand = OBJ_EXE_SHELL.executeCommand(command);
		LOGGER.info("executed snapshot image: {}, output: {}.", command, executeCommand);
	}
	
	/**
	 * Stops motion.
	 * Mostly in order to make snapshot
	 */
	public static void stopMotion() {
		//String[] command = {"/bin/sh", "-c", "sudo /etc/init.d/motion stop"};
		//String command = "sh -c sudo /etc/init.d/motion stop";
		String command = "sudo /etc/init.d/motion stop";
		//String command = "/camera-snapshots/stopMotion.sh";
		String executeCommand = OBJ_EXE_SHELL.executeCommand(command);
		LOGGER.info("executed: {}, output: {}.", command, executeCommand);		
	}
	
	/**
	 * Starts motion
	 */
	public static void startMotion() {
//		String[] command = {"/bin/sh", "-c", "sudo /etc/init.d/motion start"};
//		String command = "sh -c sudo /etc/init.d/motion start";
		String command = "sudo /etc/init.d/motion start";
		//String command = "/camera-snapshots/startMotion.sh";
		String executeCommand = OBJ_EXE_SHELL.executeCommand(command);
		LOGGER.info("executed: {}, output: {}.", command, executeCommand);
	}
	
	/**
	 * Returns file name based on given parameters
	 */
	public static String getFileName(String prefix, long timestamp, String suffix) {
		StringBuilder fileName = new StringBuilder(prefix);
		fileName.append(timestamp);
		fileName.append(".");
		fileName.append(suffix);
		return fileName.toString();
	}
	
	/**
	 * Creates subdir for the snapshot images.
	 */
	public static void createSnapshotDir(String dirName, long subDirNameTmstmp) {
		String command = "mkdir " + dirName + subDirNameTmstmp;
		String executeCommand = OBJ_EXE_SHELL.executeCommand(command);
		LOGGER.info("executed: {}, output: {}.", command, executeCommand);
	}
	
	public static void deleteSnapshotDir(String dirName, long subDirNameTmstmp) {
		String command = "rm -r " + dirName + subDirNameTmstmp;
		String executeCommand = OBJ_EXE_SHELL.executeCommand(command);
		LOGGER.info("executed: {}, output: {}.", command, executeCommand);
	}
	
	public static void zipSnapshotDir(String dirName, long subDirNameTmstmp) {
		String command = "zip -r " + dirName + subDirNameTmstmp + ".zip" + " " + dirName + subDirNameTmstmp;
		String executeCommand = OBJ_EXE_SHELL.executeCommand(command);
		LOGGER.info("executed: {}, output: {}.", command, executeCommand);
	}

	public static void changeOwnershipSnapshotDir(String dirName, long subDirNameTmstmp) {
		String command = "sudo chown motion:motion_users " + dirName + subDirNameTmstmp + ".zip";
		String executeCommand = OBJ_EXE_SHELL.executeCommand(command);
		LOGGER.info("executed: {}, output: {}.", command, executeCommand);
	}
	
	public static void deleteSnapshotFile(String dirName, String fileName) {
		String command = "rm " + dirName + fileName;
		String executeCommand = OBJ_EXE_SHELL.executeCommand(command);
		LOGGER.info("executed: {}, output: {}.", command, executeCommand);
	}
}