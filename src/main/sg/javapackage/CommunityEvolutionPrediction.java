package main.sg.javapackage;

import java.io.File;
import main.sg.javapackage.domain.GlobalVariables.Algorithm;
import main.sg.javapackage.domain.GlobalVariables.Dataset;
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
 * @param integer [1,6]; corresponding to dataset used
 *            1: facebook dataset
 *            2: enron dataset
 *            3: dblp dataset
 *            4: bioperl dataset
 *            5: user input dataset
 *
 */
public class CommunityEvolutionPrediction {

	public static void main(String[] args) throws Exception{
		
		//Declarations
		final double version = 2.2;
		boolean isWeighted = false;
		final int totalTimesteps;
		final long programStartTime = System.currentTimeMillis();
		final float programRunTime;
		Dataset datasetUsed = Dataset.other;
		Algorithm selectedAlgo = Algorithm.slpa;
		String InputPath=null;
		String OCDPath=null;
		
		System.out.println("||A FRAMEWORK FOR PREDICTIVE ANALYSIS OF TIME EVOLVING AND OVERLAPPING COMMUNITIES||");
		System.out.println("Author: Stephen Gunashekar");
		System.out.println("Version "+version);
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println("Performing Initial Checksum of the input data...");
		if(!InputManager.inputAssert(args)){
			System.out.println("	Initial Checksum failed. Exiting.");
			System.exit(0);
		}
		else
			System.out.println("	Success.");
		
		File iniFile = new File(args[0]);
		Ini preferences = new Ini(iniFile);
		Logger.setLogger();
		
		Logger.writeToLogln("Framework Start Point...");
		Logger.writeToLogln("--------------------------");
		Logger.writeToLogln("");
		
		if(Integer.parseInt(args[1]) == 1)
			datasetUsed = Dataset.facebook;
		else if(Integer.parseInt(args[1]) == 2)
			datasetUsed = Dataset.enron;
		else if(Integer.parseInt(args[1]) == 3)
			datasetUsed = Dataset.dblp;
		else if(Integer.parseInt(args[1]) == 4)
			datasetUsed = Dataset.bioperl;
		else
			datasetUsed = Dataset.other;
		totalTimesteps = Integer.parseInt(preferences.get(datasetUsed.toString(), "TotalTimesteps"));

		if(preferences.get(datasetUsed.toString(), "IsWeighted").equalsIgnoreCase("true") ){
			isWeighted = true;
		}
		if(Integer.parseInt(preferences.get("ocd","Algorithm")) == 2)
			selectedAlgo = Algorithm.dmid;
		else if (Integer.parseInt(preferences.get("ocd","Algorithm")) == 3)
			selectedAlgo = Algorithm.focs;
		else
			selectedAlgo = Algorithm.slpa;
		
		if(selectedAlgo == Algorithm.focs){
			InputPath = preferences.get(datasetUsed.toString(), "A_InputPath");
			if(Integer.parseInt(preferences.get("ocd","OverlapThreshold")) == 70){
				OCDPath = preferences.get(datasetUsed.toString(), "AFOCSPath_70");
			}
			else if(Integer.parseInt(preferences.get("ocd","OverlapThreshold")) == 75){
				OCDPath = preferences.get(datasetUsed.toString(), "AFOCSPath_75");
			}
			else
				System.out.println("Invalid OverlapThreshold Value");
		}
		else{
			InputPath = preferences.get(datasetUsed.toString(), "S_InputPath");
		}
		
		System.out.println("Selected Parameters are:- ");
		System.out.println("-------------------------");
		System.out.println("Dataset		  :: "+datasetUsed.toString().toUpperCase());
		System.out.println("Timesteps	  :: "+totalTimesteps);
		System.out.println("Algorithm 	  :: "+selectedAlgo.toString().toUpperCase());
		System.out.println("B-Threshold	  :: "+Integer.parseInt(preferences.get("ocd","OverlapThreshold")));
		System.out.println("Weighted	  :: "+isWeighted);
		
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
