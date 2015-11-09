package main.sg.javapackage.sna;

import java.util.List;

import main.sg.javapackage.domain.Community;
import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.domain.GlobalVariables.Evolution;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.ocd.OverlapCommunityDetection;

/**
 * Primary class for detecting the community transition/events.
 * -Modified implementation of GED Algorithm
 * Survive,Merge,Split,Dissolve
 * @author Stephen
 *
 */
public class EvolutionDetection {
	
	//longest number of stable evolution
	private int longestEvolution;
	
	//pointer to results file
	private static String resultFile = GlobalVariables.resultFile;
	
	//minimum community size
	int commSize = GlobalVariables.communitySizeThreshold;

	public EvolutionDetection() {
		Logger.writeToFile(resultFile,"\n\n",true);
	}
	
	/**
	 * function to perform group evolution discovery (GED)
	 */
    public void onetomanyCommunityEvolutionTracking() {
    	
    	//GED Algorithm thresholds and inclusion value
		double inclusionT1T2,alphaScore, betaScore;
		inclusionT1T2 = 0.0;
		alphaScore = 0.0;
		betaScore = 0.0;
		
		//matched community id
		int bestMatchCommunity ;

		
		Logger.writeToLogln("");
		Logger.writeToLogln("Group Evolution Discovery...");
		
		//From 1 to < N-1 as the last graph will have boundary issues.
		for(int timeT1 = 1; timeT1 < PreProcessing.totalGraphCount();timeT1++){
			
			for(int commt1 = 1; commt1 <= OverlapCommunityDetection.numOfCommunities(timeT1);commt1++){
				
				//skip very small communities
				if(OverlapCommunityDetection.sizeOfCommunity(timeT1,commt1) <= commSize){
					//Skip calculation for communities with less nodes
					Logger.writeToLogln(commt1+" Skipped due to < Threshold nodes ");
					continue;
				}
				int timeT2 = timeT1+1;
				bestMatchCommunity = 0;
				inclusionT1T2 = 0.0;
				alphaScore = 0.0;
				betaScore = 0.0;
				
				//matching Ti communities with Ti+1 communities
				for(int commt2 = 1; commt2<= OverlapCommunityDetection.numOfCommunities(timeT2) ; commt2++) {
					
					//skip small communities
					if(OverlapCommunityDetection.sizeOfCommunity(timeT2,commt2) <= commSize)
						continue;
					
					//compute inclusion value based on the formula
					inclusionT1T2 = calculateInclusion(OverlapCommunityDetection.getCommunityNodes(timeT1, commt1)
							,OverlapCommunityDetection.getCommunityNodes(timeT2, commt2));
					if(inclusionT1T2 >= alphaScore ){
						alphaScore = inclusionT1T2;
						bestMatchCommunity = commt2;
						betaScore = calculateInclusion(OverlapCommunityDetection.getCommunityNodes(timeT2, commt2)
								,OverlapCommunityDetection.getCommunityNodes(timeT1, commt1));
					}
				}
				
				//compare inclusion value with the alpha and beta thresholds to determine the event
				//refer to tree based decision making from the report
				if(alphaScore >= GlobalVariables.GED_INCLUSION_ALPHA && betaScore >= GlobalVariables.GED_INCLUSION_BETA ){
					Logger.writeToLogln(commt1+ " survives as " + bestMatchCommunity);
					OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.survive);
					
					OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(
							OverlapCommunityDetection.Communities.get(timeT1)[commt1]);
				}
				
				else if(alphaScore >= GlobalVariables.GED_INCLUSION_ALPHA && betaScore < GlobalVariables.GED_INCLUSION_BETA	){
					if(OverlapCommunityDetection.sizeOfCommunity(timeT1, commt1) < 
							OverlapCommunityDetection.sizeOfCommunity(timeT2, bestMatchCommunity)){
						Logger.writeToLogln(commt1+ " merges into " + bestMatchCommunity);
						OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.merge);
						
						OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(
								OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

					}
					else{
						Logger.writeToLogln(commt1+ " splits into " + bestMatchCommunity);
						OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.split);
						
						OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(
								OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

					}
					
				}
				
				else if(alphaScore < GlobalVariables.GED_INCLUSION_ALPHA && betaScore >= GlobalVariables.GED_INCLUSION_BETA ){
					if(OverlapCommunityDetection.sizeOfCommunity(timeT1, commt1) >= 
							OverlapCommunityDetection.sizeOfCommunity(timeT2, bestMatchCommunity)){
						Logger.writeToLogln(commt1+ " splits into " + bestMatchCommunity);
						OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.split);
						
						OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(
								OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

					}
					else{
						Logger.writeToLogln(commt1+ " merges into " + bestMatchCommunity);
						OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.merge);
						
						OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(
								OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

					}

				}
				else if(alphaScore < GlobalVariables.GED_INCLUSION_ALPHA && betaScore < GlobalVariables.GED_INCLUSION_BETA ){
					Logger.writeToLogln(commt1+ " dissolves " );
					OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.dissolve);
					
					OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(
							OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

				}
				else{
					Logger.writeToLogln(commt1+ " *UNKNOWN* " + bestMatchCommunity);
					OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.dissolve);
					
					OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(
							OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

				}
				//Alpha,Beta score printer
				//Logger.writeToLogln("Alpha: " + alphaScore + " Beta: " + betaScore + " C1size: " + 
					//OverlapCommunityDetection.sizeOfCommunity(timeT1, commt1)
						//+ " C2size: " + OverlapCommunityDetection.sizeOfCommunity(timeT2, bestMatchCommunity) );
			}
			Logger.writeToLogln("");	
		}
	}
	
	/**
	 * Calculate the inclusion value of 
	 * two communities C1 and C2
	 * from timestep i and i+1 respectively
	 * 
	 * @param community C1
	 * 
	 * @param community C2
	 */
	private double calculateInclusion(List<Node> tempCommValuesC1, List<Node> tempCommValuesC2){
		long counter=0;
		double sum_n,sum_d;
		double groupquantity, groupquality;
		groupquality = 0.0;
		groupquantity = 0.0;
		sum_n = 0.0;
		sum_d = 0.0;
		for (int listIterator1 = 0; listIterator1<tempCommValuesC1.size();listIterator1++){

			for(int listIterator2 = 0; listIterator2<tempCommValuesC2.size();listIterator2++){
				if((long) tempCommValuesC1.get(listIterator1).getId() == (long) tempCommValuesC2.get(listIterator2).getId() ){
					counter++;
					sum_n += tempCommValuesC1.get(listIterator1).getEigenCentrality();
				}
			}
			sum_d += tempCommValuesC1.get(listIterator1).getEigenCentrality();
		}
		groupquality = sum_n / sum_d;
		groupquantity = counter/(double)tempCommValuesC1.size();

		return (groupquantity * groupquality);
	}
	
	/**
	 * tracing of community evolution using recursive matching for graphical representation
	 */
	public void recursiveCommunityEvolutionTracking(){
		
		//Starting from timestep 1, track the evolution the communities
		int startTimestep;
		int totalTimesteps = PreProcessing.totalGraphCount();
		
		Logger.writeToFile(resultFile,"Recursive Group Evolution Discovery...\n",true);
		Logger.writeToFile(resultFile,"CommunityId, NodeSize, EdgeSize, N_Leaders, SizeRatio, LeaderRatio, "
				+ "Density, Cohesion, ClusterCoefficient, "
				+ "SpearmanRho, DegreeCentrality, ClosenessCentrality, EigenVectorCentrality, "
				+ "LDegreeCentrality, LClosenessCentrality, LEigenVectorCentrality \n",true);
		
		// totalTimesteps - 1, as the last but second also will not have more than 2 evolutions
		for(startTimestep = 1; startTimestep < totalTimesteps ; startTimestep++){ 
			Logger.writeToFile(resultFile,"\nTimestep "+startTimestep,true);
			Logger.writeToFile(resultFile,"\n-------------",true);
			for(int community = 1;community<=OverlapCommunityDetection.numOfCommunities(startTimestep);community++){

				//recursive matching similar to onetomanyCommunityEvolutionTracking function
				communityRecursion(community, startTimestep+1,totalTimesteps);
				if(longestEvolution>0){
					Logger.writeToFile(resultFile,"\nCommunity "+community+" survives for "+(longestEvolution+1) + " timesteps :\n",true);
					
					//print properties of evolution community
					Community temp = OverlapCommunityDetection.Communities.get(startTimestep)[community];
					Logger.writeToFile(resultFile,community+
							","+temp.getNumNodes()+
							","+ temp.getNumEdges()+ 
							","+ temp.getNumLeaders()+
							","+ temp.getAttrSizeRatio()+
							","+ temp.getAttrLeaderRatio()+
							","+ temp.getAttrDensity()+
							","+ temp.getAttrCohesion()+
							","+ temp.getAttrClusteringCoefficient()+
							","+ temp.getAttrSpearmanRho()+
							","+ temp.getAttrDegreeCentrality()+
							","+ temp.getAttrClosenessCentrality()+
							","+ temp.getAttrEigenVectorCentrality()+
							","+ temp.getAttrLeaderDegreeCentrality()+
							","+ temp.getAttrLeaderClosenessCentrality()+
							","+ temp.getAttrLeaderEigenVectorCentrality()+"\n",true);	
					
					//print the recursion of the evolution community in a csv format
					printRecursion(community, startTimestep+1, totalTimesteps);
					longestEvolution=0;
				}
			}
			Logger.writeToFile(resultFile,"\n",true);
		}

	}
	
	/**
	 * recursive function to detect community evolution
	 * @param community
	 * @param timestep
	 * @param totalTimesteps
	 */
	private void communityRecursion(int community,int timestep,int totalTimesteps){
		
		double tempScoreIc1c2,alphaScore, betaScore;
		int bestMatchCommunity;
		tempScoreIc1c2 = 0.0;
		alphaScore = 0.0;
		betaScore = 0.0;
		bestMatchCommunity = community;
		
		//base case
		if(timestep>totalTimesteps){
			return;
		}
		else {
			for(int tcomm = 1; tcomm<= OverlapCommunityDetection.numOfCommunities(timestep) ; tcomm++) {
				
				//skipe small communities
				if(OverlapCommunityDetection.sizeOfCommunity(timestep,tcomm) <= commSize)
					continue;
				
				//compute inclusion
				tempScoreIc1c2 = calculateInclusion(OverlapCommunityDetection.getCommunityNodes(timestep-1,community),
						OverlapCommunityDetection.getCommunityNodes(timestep,tcomm));
				
				if(tempScoreIc1c2 >= alphaScore){
					alphaScore = tempScoreIc1c2;
					bestMatchCommunity = tcomm;
					betaScore = calculateInclusion(OverlapCommunityDetection.getCommunityNodes(timestep,tcomm),
							OverlapCommunityDetection.getCommunityNodes(timestep-1,community));
				}
			}
			//track only the survived communities
			if(alphaScore >= GlobalVariables.GED_INCLUSION_ALPHA && betaScore >= GlobalVariables.GED_INCLUSION_BETA){
				longestEvolution++;
				communityRecursion(bestMatchCommunity, timestep+1,totalTimesteps);
			}			
		}
	}
	
	/**
	 * recursive function to print community evolution
	 * @param community
	 * @param timestep
	 * @param totalTimesteps
	 */
	private void printRecursion(int community,int timestep,int totalTimesteps){
		
		double tempScoreIc1c2,alphaScore, betaScore;
		int bestMatchCommunity;
		tempScoreIc1c2 = 0.0;
		alphaScore = 0.0;
		betaScore = 0.0;
		bestMatchCommunity = community;
		
		//base case
		if(timestep>totalTimesteps){
			return;
		}
		else {

			for(int tcomm = 1; tcomm<= OverlapCommunityDetection.numOfCommunities(timestep) ; tcomm++) {
				
				//skip small communities
				if(OverlapCommunityDetection.sizeOfCommunity(timestep,tcomm) <= commSize)
					continue;
			
				//compute inclusion
				tempScoreIc1c2 = calculateInclusion(OverlapCommunityDetection.getCommunityNodes(timestep-1,community),
						OverlapCommunityDetection.getCommunityNodes(timestep,tcomm));
				
				if(tempScoreIc1c2 >= alphaScore){
					alphaScore = tempScoreIc1c2;
					bestMatchCommunity = tcomm;
					betaScore = calculateInclusion(OverlapCommunityDetection.getCommunityNodes(timestep,tcomm),
							OverlapCommunityDetection.getCommunityNodes(timestep-1,community));
				}
			}
			
			//track only the survive event
			if(alphaScore >= GlobalVariables.GED_INCLUSION_ALPHA && betaScore >= GlobalVariables.GED_INCLUSION_BETA){
				Community temp = OverlapCommunityDetection.Communities.get(timestep)[bestMatchCommunity];
				Logger.writeToFile(resultFile,bestMatchCommunity+
						","+temp.getNumNodes()+
						","+ temp.getNumEdges()+ 
						","+ temp.getNumLeaders()+
						","+ temp.getAttrSizeRatio()+
						","+ temp.getAttrLeaderRatio()+
						","+ temp.getAttrDensity()+
						","+ temp.getAttrCohesion()+
						","+ temp.getAttrClusteringCoefficient()+
						","+ temp.getAttrSpearmanRho()+
						","+ temp.getAttrDegreeCentrality()+
						","+ temp.getAttrClosenessCentrality()+
						","+ temp.getAttrEigenVectorCentrality()+
						","+ temp.getAttrLeaderDegreeCentrality()+
						","+ temp.getAttrLeaderClosenessCentrality()+
						","+ temp.getAttrLeaderEigenVectorCentrality()+"\n",true);	
				
				printRecursion(bestMatchCommunity, timestep+1,totalTimesteps);
			}
		}
	}

}
