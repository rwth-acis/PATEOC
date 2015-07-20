package main.sg.javapackage;

import java.io.File;
import main.sg.javapackage.domain.GlobalVariables.Algorithm;
import main.sg.javapackage.domain.GlobalVariables.Dataset;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.ocd.OverlapCommunityDetection;
import main.sg.javapackage.sna.EvolutionDetection;
import main.sg.javapackage.sna.SocialNetworkAnalysis;
import main.sg.javapackage.utils.InputManager;
import main.sg.javapackage.weka.PredictiveAnalysis;
import main.sg.javapackage.weka.SupervisedLearning;

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
 *            2: socialtracesdataset
 *            3: enron dataset
 *            4: dblp dataset
 *            5: bioperl dataset
 *            6: user input dataset
 *
 */
public class CommunityEvolutionPrediction {

	public static void main(String[] args) throws Exception{
		
		//Declarations
		final double version = 2.1;
		boolean isWeighted = false;
		final int totalTimesteps;
		final long programStartTime = System.currentTimeMillis();
		final float programRunTime;
		Dataset datasetUsed = Dataset.other;
		Algorithm selectedAlgo = Algorithm.slpa;
		String InputPath=null;
		
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
			datasetUsed = Dataset.social;
		else if(Integer.parseInt(args[1]) == 3)
			datasetUsed = Dataset.enron;
		else if(Integer.parseInt(args[1]) == 4)
			datasetUsed = Dataset.dblp;
		else if(Integer.parseInt(args[1]) == 5)
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
		}
		else{
			InputPath = preferences.get(datasetUsed.toString(), "S_InputPath");

		}		
		System.out.println("Selected Parameters are:- ");
		System.out.println("-------------------------");
		System.out.println("Dataset		  :: "+datasetUsed.toString().toUpperCase());
		System.out.println("Timesteps	  :: "+totalTimesteps);
		System.out.println("Algorithm 	  :: "+selectedAlgo.toString().toUpperCase());
		System.out.println("Weighted	  :: "+isWeighted);
		
		/*-----------------------------------------------------------------------------------------------------------------*/
		
		Logger.writeToLogln("/*------------------Pre-Processor---------------------*/");
		PreProcessing prep = new PreProcessing(InputPath, totalTimesteps, selectedAlgo);
		prep.processInputNodeList();
		Logger.writeToLogln("");
		System.out.println("Stage 1- Pre-Processor : Complete");

		Logger.writeToLogln("/*-----------Overlapping-Community-Detector-----------*/");
		OverlapCommunityDetection ocd = new OverlapCommunityDetection(selectedAlgo, isWeighted, totalTimesteps);
		ocd.performOverlapCommunityDectection(InputPath,preferences.get(datasetUsed.toString(), "AFOCSPath"));
		Logger.writeToLogln("");
		System.out.println("Stage 2- Community Dectector : Complete");

		Logger.writeToLogln("/*---------------Statistical-Extractor----------------*/");
		SocialNetworkAnalysis sna = new SocialNetworkAnalysis();
		sna.extractAnalytics();
		System.out.println("Stage 3a- Statistical Extractor : Complete");

		EvolutionDetection ged = new EvolutionDetection();
		ged.onetomanyCommunityEvolutionTracking();
		ged.recursiveCommunityEvolutionTracking();
		System.out.println("Stage 3b- Group Evolution Discoverer : Complete");

		Logger.writeToLogln("/*----------------Predictive-Analysis-----------------*/");
		PredictiveAnalysis learning = new PredictiveAnalysis();
		learning.populateArffFile();
		System.out.println("Stage 4a- Prediction Formulator : Complete");

		SupervisedLearning classify = new SupervisedLearning();
		classify.performPredictiveAnalysis();
		System.out.println("Stage 4b- Supervised Learner : Complete");

		//System Runtime
		System.out.println("-------------------------------------------");
		programRunTime = (((System.currentTimeMillis() - programStartTime) / 1000) / 60);
		System.out.println("Total runtime of the program: " +programRunTime+" minutes.");
	}

}
