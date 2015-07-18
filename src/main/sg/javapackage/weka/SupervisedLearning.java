package main.sg.javapackage.weka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import main.sg.javapackage.logging.Logger;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.unsupervised.attribute.Remove;

public class SupervisedLearning {
	
	private Instances base_TrainingData;
	private Classifier[] models = { 
			// Use a set of classifiers
			new Logistic(),//logistic regression
			new NaiveBayes(),
			new J48(), // a decision tree
			new DecisionTable(),//decision table majority classifier
			new MultilayerPerceptron(), //multiple layer neural network
			new SMO() //svm
	};
	
	public SupervisedLearning() throws IOException {
		// TODO Auto-generated constructor stub
		base_TrainingData = new Instances(readDataFile("bin\\Modellingfile.arff"));
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
		
		for(int results=1; results<=3; results++){
			Instances trainingData = null;

			if(results == 1){
				trainingData = performAttributeSelection1();
			}
			else{
				trainingData = base_TrainingData;
			}
			
			trainingData.setClassIndex(trainingData.numAttributes() - 1);
			Logger.writeToLogln("Nominal Class - Prior-Processing");
			Logger.writeToLogln(trainingData.attributeStats(trainingData.numAttributes() - 1).toString());
			
			trainingData = applySMOTE(trainingData);
			trainingData = applySpreadSubsample(trainingData);
			
			// Run for each model
			for (int j = 0; j < models.length; j++) {
				
				Instances model_data = null;
				if(results == 3){
					model_data = performAttributeSelection3(trainingData, models[j]);
				}else{
					model_data = trainingData;
				}
			  
				Evaluation evaluation = new Evaluation(model_data);
				evaluation.crossValidateModel(models[j], model_data, 10, new Random(1));
				Logger.writeToLogln(":- using " + models[j].getClass().getSimpleName());
				System.out.print(":- using " + models[j].getClass().getSimpleName());
				System.out.printf(" = %.2f",evaluation.pctCorrect());
				System.out.println(" with "+model_data.numAttributes()+ " attributes");
				Logger.writeToLogln(evaluation.toSummaryString("Results\n========", false));
				Logger.writeToLogln(evaluation.toClassDetailsString());
			}
			System.out.println("");
		}
	}
	
	/**
	 * reads the arff file into Instances to 
	 * perform supervised learning
	 * 
	 * @param filename
	 * @return reader
	 */
	private static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
 
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}
	
	
	/**
	 * applies SMOTE filter to the  
	 * the training data
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private Instances applySMOTE(Instances data) throws Exception{
		
		SMOTE smote = new SMOTE();
		smote.setClassValue("0");
		smote.setInputFormat(data);
		Instances updated_traningset = Filter.useFilter(data, smote);
		Logger.writeToLogln("Nominal Class - After SMOTE");
		Logger.writeToLogln(updated_traningset.attributeStats(updated_traningset.numAttributes() - 1).toString());
		return updated_traningset;
	}
	
	
	/**
	 * applies SpreadSubsample filter to the  
	 * the training data
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private Instances applySpreadSubsample(Instances data) throws Exception{
		
		SpreadSubsample spreadsubsampler = new SpreadSubsample();
		spreadsubsampler.setDistributionSpread(1.0);
		spreadsubsampler.setInputFormat(data);
		Instances updated_traningset = Filter.useFilter(data, spreadsubsampler);
		Logger.writeToLogln("Nominal Class - After SpreadSubSample");
		Logger.writeToLogln(updated_traningset.attributeStats(updated_traningset.numAttributes() - 1).toString());
		return updated_traningset;
	}
	
	
	/**
	 * returns only the community features from 
	 * the main training data
	 * 
	 * @return Instances
	 * @throws Exception
	 */
	private Instances performAttributeSelection1() throws Exception{
		
		//Only community features
		Remove remove =  new Remove();
		remove.setAttributeIndices("1-5,last");
		remove.setInvertSelection(true);
		remove.setInputFormat(base_TrainingData);
		Instances updated_traningset = Filter.useFilter(base_TrainingData, remove);
		return updated_traningset;

	}
	/**
	 * performs attribute selection using
	 * WrapperFunction / SubsetEvalutions
	 * Bestfirst / GreedyStepwise search
	 * 
	 * @param data
	 * @param model
	 * @return Instances
	 * @throws Exception
	 */
	private Instances performAttributeSelection3(Instances data, Classifier model) throws Exception{
		
		AttributeSelection attributeselector = new AttributeSelection();
		WrapperSubsetEval wrapper = new WrapperSubsetEval();
		wrapper.setClassifier(model);
		GreedyStepwise searcher = new GreedyStepwise();
		searcher.setSearchBackwards(true);
		attributeselector.setEvaluator(wrapper);
		attributeselector.setSearch(searcher);
		attributeselector.setInputFormat(data);
		// generate new data
		Instances updated_traningset = Filter.useFilter(data, attributeselector);
		return updated_traningset;
	}
	
	/**
	 * Basic print of algorithm details
	 */
	private void printDetails(){
		
		System.out.println("Running supervised learning ...");
		Logger.writeToLogln("Classification Task Results");
		
		Logger.writeToLogln("Selected set of classifiers :");
		for(int j=0;j< models.length ; j++){
			Logger.writeToLogln("	"+models[j].getClass().getSimpleName());
		}
		Logger.writeToLogln("");
		System.out.println("Classification results :");
	}
	
}
