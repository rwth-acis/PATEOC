package main.sg.javapackage.utils;

import java.io.File;

public class InputManager {
	private static InputManager manager;
	public InputManager(){
		
	}
	protected static InputManager getManager(){
		if(manager == null)
			manager = new InputManager();
		
		return manager;
	}
	
	public static boolean inputAssert(String[] args){
		getManager();
		
		if (args.length < 2) {
			System.out.println("Error: Invalid number of input arguments.");
			System.out.println("Input format: \"Preferences.ini\" \"[dataset]\"");
			return false;
		}
		
		File iniFile = new File(args[0]);
		try{
			if(!iniFile.exists() || !iniFile.isFile()) 
				System.out.println("Error: Path to input ini file failed.");
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
