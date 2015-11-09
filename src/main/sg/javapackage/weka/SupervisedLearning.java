package main.sg.javapackage.weka;

import java.io.IOException;
import java.util.Random;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.metrics.HeatMap;
import main.sg.javapackage.metrics.StatisticalMetrics;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.Bagging;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import main.sg.javapackage.utils.FileOperator;
import main.sg.javapackage.visualize.ChartVisualization;
import main.sg.javapackage.weka.filters.*;

/**
 * Predictive analysis module using the generated arff file
 * associated with the graph with all features
 * @author Stephen
 *
 */
@SuppressWarnings("unused")
public class SupervisedLearning {
	
	//core set of training data before any processing
	private Instances base_TrainingData;
	
	//pointer to weka model file
	private static String resultFile = GlobalVariables.resultFile;
	
	//pointer to results file
	private static String modelingFile = GlobalVariables.modelingFile;
	
	// set of classifiers used for classification
	private Classifier[] models = { 
			new Logistic(),//logistic regression
			new NaiveBayes(), //probabilistic
			new J48(), // a decision tree
			new DecisionTable(),
			new Bagging(),
			new SMO(), //svm
			//new RandomForest() //tree classifier
	};

	//constructor
	public SupervisedLearning() throws IOException {
		base_TrainingData = new Instances(FileOperator.readDataFile(modelingFile));
		Logger.writeToFile(resultFile,"\nClassification Results...",true);
	}
	
	/**
	 * supervised learning using the classifiers
	 * and the training data using 10 fold
	 * cross fold validation
	 * 
	 * @throws Exception
	 */
	public void performPredictiveAnalysis() throws Exception {
		
		//initial printer
		printDetails();
		
		Instances eventBasedTrainingData;
		String accuracy = null, weightedPrecision = null, weightedFMeasure = null, weightedRecall = null;
		String result = accuracy+","+weightedPrecision+","+weightedFMeasure;
		
		//initializing chart values
		XYSeries[] series = new XYSeries[models.length];
		
		/**	For each event:
		 * 		1-survive
		 * 		2-merge
		 * 		3-split
		 * 		4-dissove
		 * 		5-multiclass
		 */
		for(int event = 1; event <= 5; event++){

			
			//initialize all attributes for statistical measuring
			StatisticalMetrics.initializeSupervisedMetrics(base_TrainingData);
			
			//aggregate all attributes and the precise label (event) from the master .arff file
			eventBasedTrainingData = null;
			eventBasedTrainingData = EventBasedInstanceFilter.eventFilter(base_TrainingData, event);

			//initialize the chart series
			for(int j=0;j< models.length ; j++){
				series[j] = new XYSeries(models[j].getClass().getSimpleName().toString());
			}

			
			/**
			 * 	spliting the feature set into categories
			 * 	class:
			 * 		1-With community features
			 * 		2-With community features + leadership features + temporal features
			 * 		3-Most suitable features picked using Machine Learning Attribute Selection
			 */
			for(int problemClass=1; problemClass<=3; problemClass++){

				Logger.writeToFile(resultFile,"Problem-Class "+problemClass+"\n",true);
				Instances trainingData = null;
				try{
					
					//for intra features
					if(problemClass == 1){
						trainingData = AttributeSelectionFilter.performAttributeSelection1(eventBasedTrainingData);
					}
					
					//otherwise (inter,selective features)
					else{
						trainingData = AttributeSelectionFilter.performAttributeSelection2(eventBasedTrainingData);
					}
					
					//set the last attribute as the label
					trainingData.setClassIndex(trainingData.numAttributes() - 1);
					
					//Before Balancing
					Logger.writeToFile(resultFile,"Nominal Class - Prior-Processing\n",true);
					Logger.writeToFile(resultFile,trainingData.attributeStats(trainingData.numAttributes() - 1).toString()+"\n",true);
					if(event!=5){
						//apply SMOTE and SubSample for class imbalance problem
						trainingData = SyntheticOversamplingFilter.applySMOTE(trainingData);
						trainingData = SpreadSubsampleFilter.applySpreadSubsample(trainingData);
					}

				}catch(Exception e){
					continue;
				}
				
				Logger.writeToFile(resultFile,"Classifier,Accuracy,Precision,Recall,FMeasure\n",true);
				
				// Run for each classification model
				for (int j = 0; j < models.length; j++) {
					try{
					
						Instances model_data = null;
						
						//for selective features
						if(problemClass == 3){
							
							//run wrapper method to perform attribute selection
							model_data = AttributeSelectionFilter.performAttributeSelection3(trainingData, models[j]);
							
							//update the statistical measures based on the selected attributes
							StatisticalMetrics.updateSupervisedMetrics(model_data);
						}
						
						//for inter features
						else{
							model_data = trainingData;
						}
						
						//classification with 10-fold cross validation
						Evaluation evaluation = new Evaluation(model_data);
						evaluation.crossValidateModel(models[j], model_data, 10, new Random(1));
						
						//metrics
						accuracy = String.format("%.3f",evaluation.pctCorrect());
						weightedPrecision = String.format("%.3f",evaluation.weightedPrecision()*100);
						weightedFMeasure = String.format("%.3f",evaluation.weightedFMeasure()*100);
						weightedRecall = String.format("%.3f",evaluation.weightedRecall()*100);

						//aggregate the metrics
						result = accuracy+","+weightedPrecision+","+weightedRecall+","+weightedFMeasure;
						
						//print metrics
						Logger.writeToFile(resultFile,models[j].getClass().getSimpleName()+","+result+"\n",true);
						Logger.writeToLogln(evaluation.toSummaryString("Results\n========", false));
						Logger.writeToLogln(evaluation.toClassDetailsString());
						
						//add metrics for chart rendering (normalizing european number format)
						series[j].add(problemClass,Double.parseDouble(accuracy.replaceAll(",", ".")));
					
					}catch(IllegalArgumentException e){
						System.out.println("Insufficient training data. Exit Code :114" );
						System.out.println("Exception : "+e.getMessage());
						System.exit(114);
					}
					
				}
				Logger.writeToFile(resultFile,"\n",true);
			}
			
			//print aggregated statistical measures in the result file
			StatisticalMetrics.printSupervisedMetrics(event);
			StatisticalMetrics.clearSupervisedMetrics();
			
			//Save results as a .png plot
			ChartVisualization.generateChart(convertResultToPlotDataset(series), event);
			
		}
		
		//create final heat map of overall selective attributes
		HeatMap.createHeatMap();
	}
	
	/**
	 * Console and logger printing of algorithmic details
	 */
	private void printDetails(){
		
		System.out.println("Running Supervised Learning Task ...");
		Logger.writeToLogln("Classification Task Results");
		
		Logger.writeToLogln("Selected set of classifiers :");
		for(int j=0;j< models.length ; j++){
			Logger.writeToLogln("	"+models[j].getClass().getSimpleName());
		}
		Logger.writeToLogln("");
	}
	
	/**
	 * convert numerical metrics in JFreeChart format
	 * @param series
	 * @return
	 */
	private XYDataset convertResultToPlotDataset(XYSeries[] series) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		for (int j = 0; j < models.length; j++) {
			dataset.addSeries(series[j]);
		}
				
		return dataset;
	}
	
}
