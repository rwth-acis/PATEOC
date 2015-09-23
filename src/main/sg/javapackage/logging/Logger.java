package main.sg.javapackage.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;

/**
 * @author Stephen
 * @category Logging
 * 
 */

public class Logger {

	private static Logger manager;
	private static File logFile;
	private static String baseDir;
	private static String fileName;
	
	public Logger(){
		
	}
	
	public static void setLogger(){
		
		Logger.baseDir = System.getProperty("user.dir").concat("\\bin\\");
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
		    //Log message with end of line
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
		    //Log message without end of line
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
