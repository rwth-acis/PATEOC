package main.sg.javapackage.weka;

import main.sg.javapackage.domain.Community;
import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.ocd.OverlapCommunityDetection;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class PredictiveAnalysis {
	
	private String arffFile = GlobalVariables.modellingFile;
	
	public PredictiveAnalysis(){
		
	}
	/**
	 * Aggregates all the computed attributes into
	 * a weka compatiable "arff" format obtaining 
	 * values from Communities Map
	 */
	public void populateArffFile(){
				
		long totalInstances = 0;
		int n_survive = 0, n_merge = 0, n_split = 0, n_dissolve = 0;
		
		FastVector      atts, attVals;
		Instances       data;
		double[]        base_vals;
		// 1. set up attributes
		atts = new FastVector();
		// - numeric
		atts.addElement(new Attribute("NNodes"));
		atts.addElement(new Attribute("SizeRatio"));
		atts.addElement(new Attribute("Density"));
		atts.addElement(new Attribute("Cohesion"));
		atts.addElement(new Attribute("ClusteringCoeffecient"));
		atts.addElement(new Attribute("DegreeCentrality"));
		atts.addElement(new Attribute("ClosenessCentrality"));
		atts.addElement(new Attribute("EigenVectorCentrality"));
		atts.addElement(new Attribute("Assortativity"));
		atts.addElement(new Attribute("NLeader"));
		atts.addElement(new Attribute("LDegreeCentrality"));
		atts.addElement(new Attribute("LClosenessCentrality"));
		atts.addElement(new Attribute("LEigenVectorCentrality"));
		atts.addElement(new Attribute("LAssortativity"));
		atts.addElement(new Attribute("D_NNodes"));
		atts.addElement(new Attribute("D_NLeader"));
		atts.addElement(new Attribute("D_SizeRatio"));
		atts.addElement(new Attribute("D_Density"));
		atts.addElement(new Attribute("D_Cohesion"));
		atts.addElement(new Attribute("D_ClusteringCoeffecient"));
		atts.addElement(new Attribute("D_DegreeCentrality"));
		atts.addElement(new Attribute("D_ClosenessCentrality"));
		atts.addElement(new Attribute("D_EigenVectorCentrality"));
		atts.addElement(new Attribute("D_Assortativity"));
		
		attVals = new FastVector();
		attVals.addElement("true");
		attVals.addElement("false");
		atts.addElement(new Attribute("PreviouSurvive", attVals));
		atts.addElement(new Attribute("PreviousMerge", attVals));
		atts.addElement(new Attribute("PreviousSplit", attVals));
		atts.addElement(new Attribute("PreviousDissolve", attVals));
		atts.addElement(new Attribute("Survive", attVals));
		atts.addElement(new Attribute("Merge", attVals));
		atts.addElement(new Attribute("Split", attVals));
		atts.addElement(new Attribute("Dissolve", attVals));


		
		// 2. create Instances object
		data = new Instances("PredictionData", atts, 0);
		
		for(int timestep = 1;timestep < PreProcessing.totalGraphCount() ; timestep++) {
			for(int community = 1; community<= OverlapCommunityDetection.numOfCommunities(timestep) ; community++) {
				
				totalInstances++;
				
				if(OverlapCommunityDetection.sizeOfCommunity(timestep,community) <= 3){
					continue;
				}
				
				Community C_i_P = OverlapCommunityDetection.Communities.get(timestep)[community];
				base_vals = new double[data.numAttributes()];
				base_vals[0] = C_i_P.getNumNodes();
				base_vals[1] = C_i_P.getAttrSizeRatio();
				base_vals[2] = C_i_P.getAttrDensity();
				base_vals[3] = C_i_P.getAttrCohesion();
				base_vals[4] = C_i_P.getAttrClusteringCoefficient();
				base_vals[5] = C_i_P.getAttrDegreeCentrality();
				base_vals[6] = C_i_P.getAttrClosenessCentrality();
				base_vals[7] = C_i_P.getAttrEigenVectorCentrality();
				base_vals[8] = C_i_P.getAttrAssortativity();
				
				base_vals[9] = C_i_P.getNumLeaders();
				base_vals[10] = C_i_P.getAttrLeaderDegreeCentrality();
				base_vals[11] = C_i_P.getAttrLeaderClosenessCentrality();
				base_vals[12] = C_i_P.getAttrLeaderEigenVectorCentrality();
				base_vals[13] = C_i_P.getAttrLeaderAssortativity();

				Community temp;
				if(C_i_P.existsPreviousCommunity()){
					temp = C_i_P.getPreviousCommunity();
				}else{
					temp = new Community();
				}
				base_vals[14] = Math.abs(C_i_P.getNumNodes() - temp.getNumNodes());
				base_vals[15] = Math.abs(C_i_P.getNumLeaders() - temp.getNumLeaders());
				base_vals[16] = Math.abs(C_i_P.getAttrSizeRatio() - temp.getAttrSizeRatio());
				base_vals[17] = Math.abs(C_i_P.getAttrDensity() - temp.getAttrDensity());
				base_vals[18] = Math.abs(C_i_P.getAttrCohesion() - temp.getAttrCohesion());
				base_vals[19] = Math.abs(C_i_P.getAttrClusteringCoefficient() - temp.getAttrClusteringCoefficient());
				base_vals[20] = Math.abs(C_i_P.getAttrDegreeCentrality() - temp.getAttrDegreeCentrality());
				base_vals[21] = Math.abs(C_i_P.getAttrClosenessCentrality() - temp.getAttrClosenessCentrality());
				base_vals[22] = Math.abs(C_i_P.getAttrEigenVectorCentrality() - temp.getAttrEigenVectorCentrality());
				base_vals[23] = Math.abs(C_i_P.getAttrAssortativity() - temp.getAttrAssortativity());
				
				base_vals[24] = attVals.indexOf("false");
				base_vals[25] = attVals.indexOf("false");
				base_vals[26] = attVals.indexOf("false");
				base_vals[27] = attVals.indexOf("false");
				
				if(temp.getEvolution() != null){
					switch (temp.getEvolution().toString().toLowerCase()) {
					case "survive":
						base_vals[24] = attVals.indexOf("true");
						break;
					
					case "merge":
						base_vals[25] = attVals.indexOf("true");
						break;
						
					case "split":
						base_vals[26] = attVals.indexOf("true");
						break;
						
					case "dissolve":
						base_vals[27] = attVals.indexOf("true");
						break;

					default:
						break;
					}
				}
				
				base_vals[28] = attVals.indexOf("false");
				base_vals[29] = attVals.indexOf("false");
				base_vals[30] = attVals.indexOf("false");
				base_vals[31] = attVals.indexOf("false");
				
				switch (C_i_P.getEvolution().toString().toLowerCase()) {
				case "survive":
					n_survive++;
					base_vals[28] = attVals.indexOf("true");
					break;
				
				case "merge":
					n_merge++;
					base_vals[29] = attVals.indexOf("true");
					break;
					
				case "split":
					n_split++;
					base_vals[30] = attVals.indexOf("true");
					break;
					
				case "dissolve":
					n_dissolve++;
					base_vals[31] = attVals.indexOf("true");
					break;

				default:
					break;
				}
				
				data.add(new Instance(1.0, base_vals));
			}
		}
		
		// 4. output data
		Logger.writeToFile(arffFile, data.toString(),false); //OutputDump
		Logger.writeToLogln("Modelling file successfully generated.");
		
		Logger.writeToLogln("Total number of instances from "+PreProcessing.totalGraphCount()+" graphs are: "+totalInstances );
		Logger.writeToLogln("SURVIVE: " +n_survive);
		Logger.writeToLogln("MERGE: " +n_merge);
		Logger.writeToLogln("SPLIT: " +n_split);
		Logger.writeToLogln("DISSOLVE: " +n_dissolve);
		
	}
		
}
