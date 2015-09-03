package main.sg.javapackage.domain;

public class GlobalVariables {
	
	public enum Dataset{facebook, enron, dblp, bioperl, other};
	public enum Algorithm{slpa, dmid, focs};
	public enum Evolution{survive,merge,split,dissolve};
	
	public static float GED_INCLUSION_ALPHA = 0.5f;
	public static float GED_INCLUSION_BETA = 0.5f;
	public static double COHESION_INFINITY = 99999999999.99999f;
	public static boolean graphExtract = false;
	public static boolean subgraphExtract = false;
	public static double leaderThreshold = 0.9f;
	public static long ocdWebServiceSleepTime = 30000L;
	public static String modelingFile;
	public static String resultFile;
	
	public GlobalVariables() {
		// TODO Auto-generated constructor stub
	}
	
	public static void setResultFile(String filename){
		resultFile = "bin\\"+filename+".txt";
	}
	
	public static void setModelingFile(String filename){
		modelingFile = "bin\\"+filename+".arff";
	}
	
}
