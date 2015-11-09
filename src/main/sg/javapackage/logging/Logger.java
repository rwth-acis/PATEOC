package main.sg.javapackage.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;

/**
 * Logger class in order to write all error/warnings/messages/results to Log (or Results) file
 * @author Stephen
 * @category Logging
 * 
 */

public class Logger {

	//Logger instance
	private static Logger manager;
	
	//Log file
	private static File logFile;
	
	//Directory of the Log file
	private static String baseDir;
	
	//Name of the Log file
	private static String fileName;
	
	public Logger(){
		
	}
	
	/**
	 * Initialize the logger
	 */
	public static void setLogger(){
		
		//Save Log file in bin folder of the project
		Logger.baseDir = System.getProperty("user.dir").concat("\\bin\\");
		
		//Log file name
		Logger.fileName = "Logfile.txt";
		
		logFile = new File(Logger.baseDir+Logger.fileName); 
		try{
			//New file for each run
			if(logFile.exists() && logFile.isFile()) 
				logFile.delete();
		}catch(Exception e) {
			System.out.println("Error: Path to logging file failed.");
			e.printStackTrace();
		}
		Logger.writeToLogln("------------------------------------------------------------------------------------");		
		Logger.writeToLogln("||A FRAMEWORK FOR PREDICTIVE ANALYSIS OF TIME EVOLVING AND OVERLAPPING COMMUNITIES||");
		Logger.writeToLogln("------------------------------------------------------------------------------------");
		Logger.writeToLogln("Framework Start Point...");
		Logger.writeToLogln("--------------------------\n");
	}
	
	/**
	 * get logger singleton
	 */
	protected static Logger getManager(){
		if(manager == null)
			manager = new Logger();
		
		return manager;
	}
	
	/**
     * Responsible for logging all messages and output generated into a logfile.
     * With end of line.
     *
     * @param message
     *            String value that needs to be logged in the file
     *
     */
	
	public static void writeToLogln(String message) {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Logger.baseDir+Logger.fileName, true)))) {
		    //Log message with end of line character
			out.println(message);
		    out.close();
		}catch (IOException e) {
			System.out.println("Error: Write operation to logfile failed.");
		    e.printStackTrace();
		}
	}
	
	/**
     * Responsible for logging all messages and output generated into a logfile.
     * Without end of line.
     *
     * @param message
     *            String value that needs to be logged in the file
     *
     */
	public static void writeToLog(String message) {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Logger.baseDir+Logger.fileName, true)))) {
		    //Log message without end of line character
			out.print(message);
		    out.close();
		}catch (IOException e) {
			System.out.println("Error: Write operation to logfile failed.");
		    e.printStackTrace();
		}
	}
	
	/**
	 * Write a message to file 
	 * @param filename - file name to write to
	 * @param message - String message to write
	 * @param append - true/false - append the message or not
	 */
	public static void writeToFile(String filename, String message,boolean append){
		try {
			FileUtils.writeStringToFile(new File(filename), message, append);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
