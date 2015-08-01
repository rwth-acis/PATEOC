package main.sg.javapackage.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class FileOperator {
	
	public FileOperator() {
		// TODO Auto-generated constructor stub
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
