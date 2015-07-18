package main.sg.javapackage.sna;

import java.util.List;

import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.domain.GlobalVariables.Evolution;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.ocd.OverlapCommunityDetection;

public class EvolutionDetection {
	
    public void onetomanyCommunityEvolutionTracking() {
    	
    	//GED Algorithm with the qualitative factor
		double inclusionT1T2,alphaScore, betaScore;
		int bestMatchCommunity ;
		inclusionT1T2 = 0.0;
		alphaScore = 0.0;
		betaScore = 0.0;
		Logger.writeToLogln("");
		Logger.writeToLogln("Group Evolution Discovery...");
		
		//From 1 to < N-1 as the last graph will have boundary issues.
		for(int timeT1 = 1; timeT1 < PreProcessing.totalGraphCount();timeT1++){
			
			for(int commt1 = 1; commt1 <= OverlapCommunityDetection.numOfCommunities(timeT1);commt1++){
				
				if(OverlapCommunityDetection.sizeOfCommunity(timeT1,commt1) < 3){
					//Skip calculation for communities with 2 or less nodes
					continue;
				}
				int timeT2 = timeT1+1;
				bestMatchCommunity = 0;
				inclusionT1T2 = 0.0;
				alphaScore = 0.0;
				betaScore = 0.0;
				for(int commt2 = 1; commt2<= OverlapCommunityDetection.numOfCommunities(timeT2) ; commt2++) {
					
					if(OverlapCommunityDetection.sizeOfCommunity(timeT2,commt2) < 3)
						continue;
					
					inclusionT1T2 = calculateInclusion(OverlapCommunityDetection.getCommunityNodes(timeT1, commt1) ,OverlapCommunityDetection.getCommunityNodes(timeT2, commt2));
					if(inclusionT1T2 >= alphaScore ){
						alphaScore = inclusionT1T2;
						bestMatchCommunity = commt2;
						betaScore = calculateInclusion(OverlapCommunityDetection.getCommunityNodes(timeT2, commt2),OverlapCommunityDetection.getCommunityNodes(timeT1, commt1));
					}
				}
				if(alphaScore >= GlobalVariables.GED_INCLUSION_ALPHA && betaScore >= GlobalVariables.GED_INCLUSION_BETA ){
						//&& withinRange(OverlapCommunityDetection.sizeOfCommunity(timeT1, commt1),OverlapCommunityDetection.sizeOfCommunity(timeT2, bestMatchCommunity),20) 
					Logger.writeToLogln(commt1+ " survives as " + bestMatchCommunity);
					OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.survive);
					
					OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(OverlapCommunityDetection.Communities.get(timeT1)[commt1]);
				}
				
				else if(alphaScore >= GlobalVariables.GED_INCLUSION_ALPHA && betaScore < GlobalVariables.GED_INCLUSION_BETA	){
					if(OverlapCommunityDetection.sizeOfCommunity(timeT1, commt1) < OverlapCommunityDetection.sizeOfCommunity(timeT2, bestMatchCommunity)){
						Logger.writeToLogln(commt1+ " merges as " + bestMatchCommunity);
						OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.survive);
						
						OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

					}
					else{
						Logger.writeToLogln(commt1+ " splits as " + bestMatchCommunity);
						OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.dissolve);
						
						OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

					}
					
				}
				
				else if(alphaScore < GlobalVariables.GED_INCLUSION_ALPHA && betaScore >= GlobalVariables.GED_INCLUSION_BETA ){
					if(OverlapCommunityDetection.sizeOfCommunity(timeT1, commt1) >= OverlapCommunityDetection.sizeOfCommunity(timeT2, bestMatchCommunity)){
						Logger.writeToLogln(commt1+ " splits as " + bestMatchCommunity);
						OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.dissolve);
						
						OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

					}
					else{
						Logger.writeToLogln(commt1+ " merges as " + bestMatchCommunity);
						OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.survive);
						
						OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

					}

				}
				else if(alphaScore < GlobalVariables.GED_INCLUSION_ALPHA && betaScore < GlobalVariables.GED_INCLUSION_BETA ){
					Logger.writeToLogln(commt1+ " splits as " + bestMatchCommunity);
					OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.dissolve);
					
					OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

				}
				else{
					Logger.writeToLogln(commt1+ " *UNKNOWN* " + bestMatchCommunity);
					OverlapCommunityDetection.Communities.get(timeT1)[commt1].setEvolution(Evolution.dissolve);
					
					OverlapCommunityDetection.Communities.get(timeT2)[bestMatchCommunity].setPreviousCommunity(OverlapCommunityDetection.Communities.get(timeT1)[commt1]);

				}
				Logger.writeToLogln("Alpha: " + alphaScore + " Beta: " + betaScore + " C1size: " + OverlapCommunityDetection.sizeOfCommunity(timeT1, commt1)
						+ " C2size: " + OverlapCommunityDetection.sizeOfCommunity(timeT2, bestMatchCommunity) );
			}
			Logger.writeToLogln("");	
		}
	}
	
	@SuppressWarnings("unused")
	private Boolean withinRange(int size1, int size2, float range){
		int low, high;
		if(size1 < size2){
			low = size1;
			high = size2;
		}
		else{
			low = size2;
			high = size1;
		}
		
		System.out.println("Value:" + (low+(low*(range/100.0f))) + " " + (low-(low*(range/100.0f))) );
		
		if(high <= (low+(low*(range/100.0f))) && high >= (low-(low*(range/100.0f)))){
			return true;
		}
		else 
			return false;
	}
	
	//TODO:Check the correctness
	public void recursiveCommunityEvolutionTracking(){
		
		Logger.writeToLogln("Recursive Group Evolution Discovery...");
		//Starting from timestep 1, track the evolution the communities
		int startTimestep = 1;

		//for(startTimestep = 1; startTimestep < PreProcessing.totalGraphCount(); startTimestep++){
			for(int community = 1;community<=OverlapCommunityDetection.numOfCommunities(startTimestep);community++){
				Logger.writeToLog(""+community);
				
				//Timestep 2 as initial
				communityRecursion(community, startTimestep+1,PreProcessing.totalGraphCount());
				Logger.writeToLogln("");
			}
			Logger.writeToLogln("");
		//}

	}
	
	private void communityRecursion(int community,int timestep,int inputFileCount){
		
		double tempScoreIc1c2,alphaScore, betaScore;
		int bestMatchCommunity;
		tempScoreIc1c2 = 0.0;
		alphaScore = 0.0;
		betaScore = 0.0;
		bestMatchCommunity = community;
		if(timestep>inputFileCount){
			return;
		}
		else {

			for(int tcomm = 1; tcomm<= OverlapCommunityDetection.numOfCommunities(timestep) ; tcomm++) {
				tempScoreIc1c2 = calculateInclusion(OverlapCommunityDetection.Communities.get(timestep-1)[community].getNodeList(),
						OverlapCommunityDetection.Communities.get(timestep)[tcomm].getNodeList());
				
				if(alphaScore <= tempScoreIc1c2 ){
					alphaScore = tempScoreIc1c2;
					bestMatchCommunity = tcomm;
					betaScore = calculateInclusion(OverlapCommunityDetection.Communities.get(timestep)[tcomm].getNodeList(),
							OverlapCommunityDetection.Communities.get(timestep-1)[community].getNodeList());
				}
			}
			if(alphaScore >= GlobalVariables.GED_INCLUSION_ALPHA && betaScore >= GlobalVariables.GED_INCLUSION_BETA){
				Logger.writeToLog("->"+bestMatchCommunity);
				communityRecursion(bestMatchCommunity, timestep+1,inputFileCount);
			}
			else{
				Logger.writeToLog("-> NOMATCH");
			}

			
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
	//TODO: Recheck the calculation
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
					//TODO:Probably consider only the leader's value
					sum_n += tempCommValuesC1.get(listIterator1).getEigenCentrality();
				}
			}
			sum_d += tempCommValuesC1.get(listIterator1).getEigenCentrality();
		}
		groupquality = sum_n / sum_d;
		groupquantity = counter/(double)tempCommValuesC1.size();
		//Logger.writeToLogln(groupquantity+ " " + groupquality + " " + (groupquantity * groupquality));

		return (groupquantity * groupquality);
	}

}
