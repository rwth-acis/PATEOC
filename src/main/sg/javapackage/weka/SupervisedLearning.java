package main.sg.javapackage.weka;

import java.io.IOException;
import java.util.Random;

import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.metrics.StatisticalMetrics;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.Bagging;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import main.sg.javapackage.utils.FileOperator;
import main.sg.javapackage.weka.filters.*;

public class SupervisedLearning {
	
	private Instances base_TrainingData;
	private static String resultFile = GlobalVariables.resultFile;
	private static String modelingFile = GlobalVariables.modelingFile;
	private Classifier[] models = { 
			// Use a set of classifiers
			new Logistic(),//logistic regression
			new BayesNet(),
			new NaiveBayes(),
			new J48(), // a decision tree
			new DecisionTable(),//decision table majority classifier
			new SimpleCart(), //classification and regression trees
			new Bagging(),
			new SMO() //svm
	};

	public SupervisedLearning() throws IOException {
		base_TrainingData = new Instances(FileOperator.readDataFile(modelingFile));
		Logger.writeToFile(resultFile,"\n\nClassification Results \n",true);
	}
	
	/**
	 * supervised learning using the classifiers
	 * and the training data using 10 fold
	 * cross fold validation
	 * 
	 * @throws Exception
	 */
	public void performPredictiveAnalysis() throws Exception {
		
		printDetails();
		Instances eventBasedTrainingData;
		
		for(int event = 1; event <= 4; event++){
			/*	Events:
			 * 		1-survive
			 * 		2-merge
			 * 		3-split
			 * 		4-dissove
			 */
			
			eventBasedTrainingData = null;
			eventBasedTrainingData = EventBasedInstanceFilter.eventFilter(base_TrainingData, event);
			
			for(int j=0;j< models.length ; j++){
				Logger.writeToFile(resultFile,models[j].getClass().getSimpleName()+" ", true);
			}
			Logger.writeToFile(resultFile,"\n",true);

			for(int results=1; results<=3; results++){
				/*
				 * 	Results:
				 * 		1-With community features
				 * 		2-With community features + leadership features + temporal features
				 * 		3-Most suitable features picked using Machine Learning Attribute Selection
				 */
				
				Instances trainingData = null;
				if(results == 1){
					trainingData = AttributeSelectionFilter.performAttributeSelection1(eventBasedTrainingData);
				}
				else{
					trainingData = AttributeSelectionFilter.performAttributeSelection2(eventBasedTrainingData);
				}
				
				trainingData.setClassIndex(trainingData.numAttributes() - 1);
				Logger.writeToLogln("Nominal Class - Prior-Processing");
				Logger.writeToLogln(trainingData.attributeStats(trainingData.numAttributes() - 1).toString());
				
				trainingData = SyntheticOversamplingFilter.applySMOTE(trainingData);
				trainingData = SpreadSubsampleFilter.applySpreadSubsample(trainingData);

				// Run for each model
				for (int j = 0; j < models.length; j++) {
					
					Instances model_data = null;
					if(results == 3){
						model_data = AttributeSelectionFilter.performAttributeSelection3(trainingData, models[j]);
						StatisticalMetrics.updateSupervisedMetrics(model_data);
					}else{
						model_data = trainingData;
					}
					Evaluation evaluation = new Evaluation(model_data);
					evaluation.crossValidateModel(models[j], model_data, 10, new Random(1));
					//For per Algorithm results
					//Logger.writeToLogln(":- using " + models[j].getClass().getSimpleName());
					//Logger.writeToFile(resultFile,":- using " + models[j].getClass().getSimpleName(),true);
					Logger.writeToFile(resultFile,evaluation.pctCorrect()+" ",true);
					//Logger.writeToFile(resultFile," with "+(model_data.numAttributes()-1)+ " features\n",true); //only features
					Logger.writeToLogln(evaluation.toSummaryString("Results\n========", false));
					Logger.writeToLogln(evaluation.toClassDetailsString());
				}
				Logger.writeToFile(resultFile,"\n",true);
			}
			//For per event split of metrics
			//StatisticalMetrics.printSupervisedMetrics();
			//StatisticalMetrics.clearSupervisedMetrics();
		}
		//Aggregated metric for all events
		StatisticalMetrics.printSupervisedMetrics();

	}
	
	/**
	 * Console and logger printing of algorithmic details
	 */
	private void printDetails(){
		
		System.out.println("Running supervised learning ...");
		Logger.writeToLogln("Classification Task Results");
		
		Logger.writeToLogln("Selected set of classifiers :");
		for(int j=0;j< models.length ; j++){
			Logger.writeToLogln("	"+models[j].getClass().getSimpleName());
		}
		Logger.writeToLogln("");
	}
	
}
