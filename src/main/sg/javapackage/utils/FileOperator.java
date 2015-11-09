package main.sg.javapackage.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * support function for file operations
 * @author Stephen
 *
 */
public class FileOperator {
	
	public FileOperator() {
		
	}
	
	/**
	 * reads the arff file into Instances to 
	 * perform supervised learning
	 * 
	 * @param filename
	 * @return reader
	 */
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
 
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}

}
