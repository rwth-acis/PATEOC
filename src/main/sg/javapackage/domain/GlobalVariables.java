package main.sg.javapackage.domain;

public class GlobalVariables {
	
	public enum Dataset{social, facebook, bioperl, enron, dblp, other};
	public enum Algorithm{slpa, dmid, focs};
	public enum Evolution{survive,merge,split,dissolve};
	
	public static String FileFormat;
	public static float GED_INCLUSION_ALPHA = 0.5f;
	public static float GED_INCLUSION_BETA = 0.5f;
	public static float COHESION_INFINITY = 99999.99999f;
	
	public GlobalVariables() {
		// TODO Auto-generated constructor stub
	}
	
}
