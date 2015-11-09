package main.sg.javapackage.domain;

/**
 * Container to hold global parameters for heuristic tweaking
 * @author Stephen
 *
 */
public class GlobalVariables {
	
	/**
	 * Set of input algorithms
	 */
	public enum Algorithm{slpa, dmid, focs};
	
	/**
	 * Set of community events
	 */
	public enum Evolution{survive,merge,split,dissolve};
	
	/**
	 * GED alpha threshold
	 */
	public static float GED_INCLUSION_ALPHA = 0.5f;

	/**
	 * GED beta threshold
	 */
	public static float GED_INCLUSION_BETA = 0.7f;
	
	/**
	 * Infinity ceiling value 
	 */
	public static double COHESION_INFINITY = 99999999999.99999f;
	
	/**
	 * Flag to indicate local graph export (.graphml format) 
	 */
	public static boolean graphExtract = false;
	
	/**
	 * Flag to indicate local subgraph export (.graphml format)
	 */
	public static boolean subgraphExtract = false;
	
	/**
	 * Flag to normalize feature values between [0,1]
	 */
	public static boolean normalizeFeatures = false;
	
	/**
	 * Flag to run OCD service locally
	 */
	public static boolean runLocalVersion = true;
	
	/**
	 * Leader nodes defining threshold 
	 */
	public static double leaderThreshold = 0.8f;
	
	/**
	 * Value in seconds indicating wait time for online OCD service to complete 
	 */
	public static long ocdWebServiceSleepTime = 25000L;
	
	/**
	 * At most size of community that are considered for modelling process 
	 */
	public static int communitySizeThreshold = 3; //Discard community with nodes less than threshold
	
	/**
	 * Output modelling file 
	 */
	public static String modelingFile;
	
	/**
	 * Ouput results file 
	 */
	public static String resultFile;
	
	/**
	 * Constructor
	 */
	public GlobalVariables() {

	}
	
	/**
	 * File name setters
	 * @param filename name of file
	 */
	public static void setResultFile(String filename){
		resultFile = "bin\\"+filename+".txt";
	}
	
	public static void setModelingFile(String filename){
		modelingFile = "bin\\"+filename+".arff";
	}
	
}
