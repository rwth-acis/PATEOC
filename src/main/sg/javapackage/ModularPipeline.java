package main.sg.javapackage;

import main.sg.javapackage.domain.GlobalVariables.Algorithm;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.ocd.OverlapCommunityDetection;
import main.sg.javapackage.sna.EvolutionDetection;
import main.sg.javapackage.sna.SocialNetworkAnalysis;
import main.sg.javapackage.weka.PredictiveAnalysis;
import main.sg.javapackage.weka.SupervisedLearning;
/**
 * Pipeline implementation of the framework - PATEOC
 * @author Stephen
 *
 */
public class ModularPipeline {
	/**
	 * Local variables - parameter holders
	 */
	private String InputPath=null;
	private int totalTimesteps=0;
	private Algorithm selectedAlgo;
	private boolean isWeighted;
	private String OCDPath=null;
	
	public ModularPipeline() {

	}
	/**
	 * Constructor
	 * @param InputPath input path to first snapshot of the graph
	 * @param totalTimesteps total input graphs in the time series
	 * @param selectedAlgo OCD algorithm
	 * @param isWeighted weighted/unweighed
	 * @param OCDPath path to cover, if AFOCS was selected
	 */
	public ModularPipeline(String InputPath,int totalTimesteps,
			Algorithm selectedAlgo,boolean isWeighted,String OCDPath){
		this.InputPath = InputPath;
		this.totalTimesteps = totalTimesteps;
		this.selectedAlgo = selectedAlgo;
		this.isWeighted = isWeighted;
		this.OCDPath = OCDPath;
	}
	/**
	 * Community evolution pipeline
	 */
	public void CommunityEvolutionPipeline() {
		try{
			/**
			 * Pre-Processor
			 */
			Logger.writeToLogln("/*------------------Pre-Processor---------------------*/");
			System.out.print("Stage 1== Pre-Processor ");
			PreProcessing prep = new PreProcessing(this.InputPath, this.totalTimesteps, this.selectedAlgo);
			prep.preprocessInputGraph();
			Logger.writeToLogln("");
			System.out.println(": Complete\n");
	
			/**
			 * Overlapping-Community-Detector
			 */
			Logger.writeToLogln("/*-----------Overlapping-Community-Detector-----------*/");
			System.out.println("Stage 2== Community Dectector ");
			OverlapCommunityDetection ocd = new OverlapCommunityDetection(selectedAlgo, isWeighted, totalTimesteps);
			ocd.performOverlapCommunityDectection(InputPath,OCDPath);
			Logger.writeToLogln("");
			System.out.println("Stage 2== : Complete\n");
	
			/**
			 * Statistical-Extractor
			 */
			Logger.writeToLogln("/*---------------Statistical-Extractor----------------*/");
			System.out.println("Stage 3a== Statistical Extractor ");
			SocialNetworkAnalysis sna = new SocialNetworkAnalysis();
			sna.extractAnalytics();
			System.out.println("Stage 3a== : Complete");
	
			/**
			 * Group Evolution Discoverer
			 */
			System.out.println("Stage 3b== Group Evolution Discoverer ");
			EvolutionDetection ged = new EvolutionDetection();
			ged.onetomanyCommunityEvolutionTracking();
			ged.recursiveCommunityEvolutionTracking();
			System.out.println("Stage 3b== : Complete\n");
	
			/**
			 * Predictive-Analyzer
			 */
			Logger.writeToLogln("/*----------------Predictive-Analyzer-----------------*/");
			System.out.println("Stage 4a== Prediction Formulator ");
			PredictiveAnalysis learning = new PredictiveAnalysis();
			learning.populateArffFile();
			System.out.println("Stage 4a== : Complete");
	
			/**
			 * Supervised Learner
			 */
			System.out.println("Stage 4b== Supervised Learner ");
			SupervisedLearning classify = new SupervisedLearning();
			classify.performPredictiveAnalysis();
			System.out.println("Stage 4b== : Complete\n");
		
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("Exception occured in current module. Exit code :102");
			System.exit(102);
		}
	}

}
