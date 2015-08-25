package main.sg.javapackage.domain;

public class GlobalVariables {
	
	public enum Dataset{facebook, enron, dblp, bioperl, other};
	public enum Algorithm{slpa, dmid, focs};
	public enum Evolution{survive,merge,split,dissolve};
	
	public static String FileFormat;
	public static float GED_INCLUSION_ALPHA = 0.5f;
	public static float GED_INCLUSION_BETA = 0.5f;
	public static float COHESION_INFINITY = 999999.99999f;
	public static boolean subgraphExtract = false;
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
