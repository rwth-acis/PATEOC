package main.sg.javapackage;

import main.sg.javapackage.domain.GlobalVariables.Algorithm;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.ocd.OverlapCommunityDetection;
import main.sg.javapackage.sna.EvolutionDetection;
import main.sg.javapackage.sna.SocialNetworkAnalysis;
import main.sg.javapackage.weka.PredictiveAnalysis;
import main.sg.javapackage.weka.SupervisedLearning;

public class ModularPipeline {
	
	private String InputPath=null;
	private int totalTimesteps=0;
	private Algorithm selectedAlgo;
	private boolean isWeighted;
	private String OCDPath=null;
	
	public ModularPipeline() {
		// TODO Auto-generated constructor stub
	}
	
	public ModularPipeline(String InputPath,int totalTimesteps,
			Algorithm selectedAlgo,boolean isWeighted,String OCDPath){
		this.InputPath = InputPath;
		this.totalTimesteps = totalTimesteps;
		this.selectedAlgo = selectedAlgo;
		this.isWeighted = isWeighted;
		this.OCDPath = OCDPath;
	}
	
	public void CommunityEvolutionPipeline() {
		try{
			
			Logger.writeToLogln("/*------------------Pre-Processor---------------------*/");
			System.out.print("Stage 1== Pre-Processor ");
			PreProcessing prep = new PreProcessing(this.InputPath, this.totalTimesteps, this.selectedAlgo);
			prep.processInputNodeList();
			Logger.writeToLogln("");
			System.out.println(": Complete\n");
	
			Logger.writeToLogln("/*-----------Overlapping-Community-Detector-----------*/");
			System.out.print("Stage 2== Community Dectector ");
			OverlapCommunityDetection ocd = new OverlapCommunityDetection(selectedAlgo, isWeighted, totalTimesteps);
			ocd.performOverlapCommunityDectection(InputPath,OCDPath);
			Logger.writeToLogln("");
			System.out.println(": Complete\n");
	
			Logger.writeToLogln("/*---------------Statistical-Extractor----------------*/");
			System.out.println("Stage 3a== Statistical Extractor ");
			SocialNetworkAnalysis sna = new SocialNetworkAnalysis();
			sna.extractAnalytics();
			System.out.println("Stage 3a== : Complete");
	
			System.out.println("Stage 3b== Group Evolution Discoverer ");
			EvolutionDetection ged = new EvolutionDetection();
			ged.onetomanyCommunityEvolutionTracking();
			ged.recursiveCommunityEvolutionTracking();
			System.out.println("Stage 3b== : Complete\n");
	
			Logger.writeToLogln("/*----------------Predictive-Analysis-----------------*/");
			System.out.println("Stage 4a== Prediction Formulator ");
			PredictiveAnalysis learning = new PredictiveAnalysis();
			learning.populateArffFile();
			System.out.println("Stage 4a== : Complete");
	
			System.out.println("Stage 4b== Supervised Learner ");
			SupervisedLearning classify = new SupervisedLearning();
			classify.performPredictiveAnalysis();
			System.out.println("Stage 4b== : Complete\n");
		
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("Exception Occured in Pipiline Architecture. Exit code :102");
			System.exit(102);
		}
	}

}
