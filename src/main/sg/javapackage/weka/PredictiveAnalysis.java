package main.sg.javapackage.weka;

import main.sg.javapackage.domain.Community;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.ocd.OverlapCommunityDetection;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class PredictiveAnalysis {
	
	private String arffFile;
	private boolean isAnyNaN;
	
	public PredictiveAnalysis(){
		this.arffFile = System.getProperty("user.dir").concat("\\bin\\Modellingfile.arff");
		this.isAnyNaN = false;
		
	}
	
	public void populateArffFile(){
		
		System.out.println("Running predictive analysis ...");
		
		long totalInstances = 0;
		@SuppressWarnings("unused")
		int n_survive = 0, n_merge = 0, n_split = 0, n_dissolve = 0;
		
		FastVector      atts, attVals;
		Instances       data;
		double[]        vals;
		// 1. set up attributes
		atts = new FastVector();
		// - numeric
		atts.addElement(new Attribute("SizeRatio"));
		atts.addElement(new Attribute("LeaderRatio"));
		atts.addElement(new Attribute("Density"));
		atts.addElement(new Attribute("Cohesion"));
		atts.addElement(new Attribute("ClusteringCoeffecient"));
		atts.addElement(new Attribute("Assortativity"));
		atts.addElement(new Attribute("DegreeCentrality"));
		atts.addElement(new Attribute("ClosenessCentrality"));
		
		atts.addElement(new Attribute("D_SizeRatio"));
		atts.addElement(new Attribute("D_LeaderRatio"));
		atts.addElement(new Attribute("D_Density"));
		atts.addElement(new Attribute("D_Cohesion"));
		atts.addElement(new Attribute("D_ClusteringCoeffecient"));
		atts.addElement(new Attribute("D_Assortativity"));
		atts.addElement(new Attribute("D_DegreeCentrality"));
		atts.addElement(new Attribute("D_ClosenessCentrality"));
		
		attVals = new FastVector();
		attVals.addElement("survive");
//		attVals.addElement("merge");
//		attVals.addElement("split");
		attVals.addElement("dissolve");
		
//		attVals = new FastVector();
//		attVals.addElement("true");
//		attVals.addElement("false");
		
//		atts.addElement(new Attribute("Survive", attVals));
//		atts.addElement(new Attribute("Merge", attVals));
//		atts.addElement(new Attribute("Split", attVals));
//		atts.addElement(new Attribute("Dissolve", attVals));
		atts.addElement(new Attribute("Evolution", attVals));


		
		// 2. create Instances object
		data = new Instances("PredictionData", atts, 0);
		
		for(int timestep = 1;timestep < PreProcessing.totalGraphCount() ; timestep++) {
			for(int community = 1; community<= OverlapCommunityDetection.numOfCommunities(timestep) ; community++) {
				
				totalInstances++;
				
				if(OverlapCommunityDetection.sizeOfCommunity(timestep,community) <= 3){
					continue;
				}
				
				Community C_i_P = OverlapCommunityDetection.Communities.get(timestep)[community];				
				vals = new double[data.numAttributes()];
				vals[0] = C_i_P.getAttrSizeRatio();
				vals[1] = C_i_P.getAttrLeaderRatio();
				vals[2] = C_i_P.getAttrDensity();
				vals[3] = C_i_P.getAttrCohesion();
				vals[4] = C_i_P.getAttrClusteringCoefficient();
				vals[5] = C_i_P.getAttrAssortativity();
				vals[6] = C_i_P.getAttrDegreeCentrality();
				vals[7] = C_i_P.getAttrClosenessCentrality();
				
				if(C_i_P.existsPreviousCommunity()){
					Community temp = C_i_P.getPreviousCommunity();
					vals[8] = Math.abs(C_i_P.getAttrSizeRatio() - temp.getAttrSizeRatio());
					vals[9] = Math.abs(C_i_P.getAttrLeaderRatio() -	temp.getAttrLeaderRatio());
					vals[10] = Math.abs(C_i_P.getAttrDensity() - temp.getAttrDensity());
					vals[11] = Math.abs(C_i_P.getAttrCohesion() - temp.getAttrCohesion());
					vals[12] = Math.abs(C_i_P.getAttrClusteringCoefficient() - temp.getAttrClusteringCoefficient());
					vals[13] = Math.abs(C_i_P.getAttrAssortativity() - temp.getAttrAssortativity());
					vals[14] = Math.abs(C_i_P.getAttrDegreeCentrality() - temp.getAttrDegreeCentrality());
					vals[15] = Math.abs(C_i_P.getAttrClosenessCentrality() - temp.getAttrClosenessCentrality());
					
				}
				else{
					vals[8] = Math.abs(C_i_P.getAttrSizeRatio());
					vals[9] = Math.abs(C_i_P.getAttrLeaderRatio());
					vals[10] = Math.abs(C_i_P.getAttrDensity());
					vals[11] = Math.abs(C_i_P.getAttrCohesion());
					vals[12] = Math.abs(C_i_P.getAttrClusteringCoefficient());
					vals[13] = Math.abs(C_i_P.getAttrAssortativity());
					vals[14] = Math.abs(C_i_P.getAttrDegreeCentrality());
					vals[15] = Math.abs(C_i_P.getAttrClosenessCentrality());
				}
				
//				vals[7] = attVals.indexOf("false");
//				vals[8] = attVals.indexOf("false");
//				vals[9] = attVals.indexOf("false");
//				vals[10] = attVals.indexOf("false");
//				
				switch (C_i_P.getEvolution().toString().toLowerCase()) {
				case "survive":
					n_survive++;
					//vals[7] = attVals.indexOf("true");
					break;
				
//				case "merge":
//					n_merge++;
//					vals[8] = attVals.indexOf("true");
//					break;
//					
//				case "split":
//					n_split++;
//					vals[9] = attVals.indexOf("true");
//					break;
					
				case "dissolve":
					n_dissolve++;
					//vals[10] = attVals.indexOf("true");
					break;

				default:
					break;
				}
				
				vals[16] = attVals.indexOf(C_i_P.getEvolution().toString().toLowerCase());
//				vals[8] = attVals.indexOf(C_i_P.getEvolution().toString().toLowerCase());
//				vals[9] = attVals.indexOf(C_i_P.getEvolution().toString().toLowerCase());
//				vals[10] = attVals.indexOf(C_i_P.getEvolution().toString().toLowerCase());

				if(Double.isNaN(vals[0]) || Double.isNaN(vals[1]) || Double.isNaN(vals[2]) || Double.isNaN(vals[3])) {
					this.isAnyNaN = true;
				}
				else
					data.add(new Instance(1.0, vals));
			}
		}
		
		// 4. output data
		Logger.writeToFile(arffFile, data.toString());
		Logger.writeToLogln("Modelling file successfully generated.");
		Logger.writeToLogln("any NaN values? :: " + this.isAnyNaN);
		
		Logger.writeToLogln("Total number of instances from "+PreProcessing.totalGraphCount()+" graphs are: "+totalInstances );
		Logger.writeToLogln("SURVIVE: " +n_survive);
//		Logger.writeToLogln("MERGE: " +n_merge);
//		Logger.writeToLogln("SPLIT: " +n_split);
		Logger.writeToLogln("DISSOLVE: " +n_dissolve);

		System.out.println("Prediction task 1 completed.");

	}
	
	public void classificationTask(){
		
	}

}
