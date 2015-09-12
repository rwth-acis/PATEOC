package main.sg.javapackage;

import java.io.File;
import java.io.IOException;

import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.domain.GlobalVariables.Algorithm;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.utils.InputManager;

import org.ini4j.Ini;


/**
 * A Framework for Predictive Analysis of Time Evolving and Overlapping Communities
 * 
 * @author Stephen Gunashekar
 * @category Main class
 * @param .ini file;
 *            path to the ini file with all the config values 
 * 
 * @param string dataset;
 * 			  name of the datset to run the application as given in the ini file
 *
 */
public class CommunityEvolutionPrediction {

	public static void main(String[] args) {
		
		//Declarations
		final double version = 4.1; 
		boolean isWeighted = false;
		final long programStartTime = System.currentTimeMillis();
		final float programRunTime;
		int totalTimesteps = 0;
		String datasetUsed = null;
		Algorithm selectedAlgo = Algorithm.slpa;
		String InputPath=null;
		String OCDPath=null;
		
		System.out.println("||A FRAMEWORK FOR PREDICTIVE ANALYSIS OF TIME EVOLVING AND OVERLAPPING COMMUNITIES||");
		System.out.println("Author: Stephen Gunashekar");
		System.out.println("Version "+version);
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println("Performing Initial Checksum of the input data...");
		if(!InputManager.inputAssert(args)){
			System.out.println("	Initial Checksum failed. Exit Code :101.");
			System.exit(101);
		}
		else
			System.out.println("	Input path verified.");

		File iniFile = null;
		Ini preferences = null;
		
		try{
			iniFile = new File(args[0]);
			preferences = new Ini(iniFile);
			datasetUsed = args[1];

			Logger.setLogger();
			Logger.writeToLogln("Framework Start Point...");
			Logger.writeToLogln("--------------------------");
			Logger.writeToLogln("");
			
			totalTimesteps = Integer.parseInt(preferences.get(datasetUsed.toString(), "TotalTimesteps"));
	
			if(preferences.get(datasetUsed.toString(), "IsWeighted").equalsIgnoreCase("true") ){
				isWeighted = true;
			}
			if(Integer.parseInt(preferences.get("ocd","Algorithm")) == 1){
				selectedAlgo = Algorithm.slpa;
				InputPath = preferences.get(datasetUsed.toString(), "InputPath1.2");
				OCDPath = null;
			}
			else if(Integer.parseInt(preferences.get("ocd","Algorithm")) == 2){
				selectedAlgo = Algorithm.dmid;
				InputPath = preferences.get(datasetUsed.toString(), "InputPath1.2");
				OCDPath = null;
			}
			else if (Integer.parseInt(preferences.get("ocd","Algorithm")) == 3){
				selectedAlgo = Algorithm.focs;
				InputPath = preferences.get(datasetUsed.toString(), "InputPath3");
				OCDPath = preferences.get(datasetUsed.toString(), "CoverPath3");
			}
			else{
				System.out.println("Unknown algorithm selected.");
			}
			
			System.out.println("Selected Parameters are:- ");
			System.out.println("-------------------------");
			System.out.println("Dataset		  :: "+datasetUsed.toString().toUpperCase());
			System.out.println("Timesteps	  :: "+totalTimesteps);
			System.out.println("Algorithm 	  :: "+selectedAlgo.toString().toUpperCase());
			System.out.println("Weighted	  :: "+isWeighted);
			GlobalVariables.setResultFile("Results_"+datasetUsed.toString().toUpperCase()
					+"_"+selectedAlgo.toString().toUpperCase());
			GlobalVariables.setModelingFile("Model_"+datasetUsed.toString().toUpperCase()
					+"_"+selectedAlgo.toString().toUpperCase());
		
		}catch(IOException e){
			System.out.println(e.getMessage()+"\nError Opening INI file. Exit Code :101");
			System.exit(101);
		}catch(NumberFormatException n){
			System.out.println(n.getMessage()+"\n Invalid Dataset. Exit Code :101");
			System.exit(101);
		}
		
		/*-----------------------------------------------------------------------------------------------------------------*/
		ModularPipeline pipeline = new ModularPipeline(InputPath, totalTimesteps, selectedAlgo, isWeighted, OCDPath);
		pipeline.CommunityEvolutionPipeline();
		/*-----------------------------------------------------------------------------------------------------------------*/

		//System Runtime
		System.out.println("-------------------------------------------");
		programRunTime = (((System.currentTimeMillis() - programStartTime) / 1000) / 60);
		System.out.println("Total runtime of the program: " +programRunTime+" minutes.");
	}

}
