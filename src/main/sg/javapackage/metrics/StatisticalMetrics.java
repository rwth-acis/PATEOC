package main.sg.javapackage.metrics;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.logging.Logger;
import weka.core.Instances;

public class StatisticalMetrics {
	
	public static Map<String,Integer> metricCounter = new HashMap<String, Integer>();
	
	private static String resultFile = GlobalVariables.resultFile;
	private static ValueComparator tempComparator =  new ValueComparator(metricCounter);
    private static TreeMap<String,Integer> sorted_metricCounter = new TreeMap<String,Integer>(tempComparator);
    
	public StatisticalMetrics() {
		// TODO Auto-generated constructor stub
	}
	
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
	
	public static void printSupervisedMetrics(){
		sorted_metricCounter.putAll(metricCounter);
		Logger.writeToFile(resultFile,"Attribute frequencies from wrapper method\n",true);
		for(Map.Entry<String, Integer> entry : sorted_metricCounter.entrySet()){
			Logger.writeToFile(resultFile,"Feature :" + entry.getKey() + " Frequency :" + entry.getValue()+"\n",true); //OutputDump
		}
		sorted_metricCounter.clear();
		
	}
	
	public static void clearSupervisedMetrics(){
		metricCounter.clear();
		//sorted_metricCounter.clear();
	}
	/**
	 * Comparator
	 * @author Stephen
	 *
	 */
	static class ValueComparator implements Comparator<String> {

	    Map<String, Integer> base;
	    public ValueComparator(Map<String, Integer> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(String a, String b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	

}
