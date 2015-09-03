package main.sg.javapackage.metrics;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
	private static NewComparator tempComparator =  new NewComparator(metricCounter);
    private static TreeMap<String,Integer> sorted_metricCounter = new TreeMap<String,Integer>(tempComparator);
    
	public StatisticalMetrics() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * updates the counter for each feature in the training data passed.
	 * 
	 * @param data - training data
	 */
	public static void updateSupervisedMetrics(Instances data){
		int num_attributes = data.numAttributes()-1; //skipping the predictor nominal class
		
		for(int i=0; i<num_attributes; i++){
			
			if(!metricCounter.containsKey(data.attribute(i).name().toLowerCase())){
				metricCounter.put(data.attribute(i).name().toLowerCase(), 1);
			}
			else{
				int val = metricCounter.get(data.attribute(i).name().toLowerCase());
				metricCounter.put(data.attribute(i).name().toLowerCase(), ++val);
			}
		}
		
	}
	
	/**
	 * prints the details from the counter hashmap
	 * in a sorted manner
	 */
	public static void printSupervisedMetrics(){
		sorted_metricCounter.putAll(metricCounter);
		Logger.writeToFile(resultFile,"\nAttribute frequencies from wrapper method\n",true);
		for(Map.Entry<String, Integer> entry : sorted_metricCounter.entrySet()){
			Logger.writeToFile(resultFile,"Feature: " + entry.getKey() + ", Frequency :" + entry.getValue()+"\n",true); //OutputDump
		}
		sorted_metricCounter.clear();
	}
	
	/**
	 * clear the counter hashmap values
	 */
	public static void clearSupervisedMetrics(){
		metricCounter.clear();
	}
	
	/**
	 * Comparator to sort the HashMap values using TreeMap
	 * @author Stephen
	 *
	 */
	static class NewComparator implements Comparator<String> {

	    Map<String, Integer> base;
	    public NewComparator(Map<String, Integer> base) {
	        this.base = base;
	    }
	    public int compare(String a, String b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        }
	    }
	}
	

}
