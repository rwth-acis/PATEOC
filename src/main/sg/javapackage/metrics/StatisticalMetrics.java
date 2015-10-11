package main.sg.javapackage.metrics;

import java.util.HashMap;
import java.util.Map;
import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.logging.Logger;
import weka.core.Instances;

/**
 * Aggregates the most commonly used feature based 
 * on the selection from feature selection - wrapper method
 * Acts as a metric for evaluation
 * @author Stephen
 *
 */
public class StatisticalMetrics {
	
	private static String resultFile = GlobalVariables.resultFile;
	public static Map<String,Integer> metricCounter = new HashMap<String, Integer>();
    
	public StatisticalMetrics() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * initialize attributes
	 */
	public static void initializeSupervisedMetrics(Instances data){
		int num_attributes = data.numAttributes()-5; //skipping all 5 nominal class
		
		for(int i=0; i<num_attributes; i++){
			metricCounter.put(data.attribute(i).name(), 0);
			
		}
		
	}
	/**
	 * updates the counter for each feature in the training data passed.
	 * 
	 * @param data - training data
	 */
	public static void updateSupervisedMetrics(Instances data){
		int num_attributes = data.numAttributes()-1; //skipping the target class
		
		for(int i=0; i<num_attributes; i++){
			
			if(!metricCounter.containsKey(data.attribute(i).name())){
				metricCounter.put(data.attribute(i).name(), 1);
			}
			else{
				int val = metricCounter.get(data.attribute(i).name());
				metricCounter.put(data.attribute(i).name(), ++val);
			}
		}
		
	}
	
	/**
	 * prints the details from the counter hashmap
	 * in a sorted manner
	 */
	public static void printSupervisedMetrics(int event){
		Logger.writeToFile(resultFile,"\nAttribute frequencies from wrapper method\n",true);
		for(Map.Entry<String, Integer> entry : metricCounter.entrySet()){
			Logger.writeToFile(resultFile,entry.getKey() + " , " + entry.getValue()+"\n",true); //OutputDump
		}
		HeatMap.aggregateHeatMap(metricCounter, event);
		metricCounter.clear();
	}
	
	/**
	 * clear the counter hashmap values
	 */
	public static void clearSupervisedMetrics(){
		metricCounter.clear();
	}
	
	

}
