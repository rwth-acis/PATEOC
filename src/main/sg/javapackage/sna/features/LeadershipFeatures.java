package main.sg.javapackage.sna.features;

import java.util.List;

import main.sg.javapackage.domain.CustomSubgraph;
import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.ocd.OverlapCommunityDetection;

/**
 * Support class for feature extraction
 * -Leadership Level
 * @author Stephen
 *
 */
public class LeadershipFeatures {
	
	private double leadershipThreshold;
	public LeadershipFeatures() {
		// TODO Auto-generated constructor stub
	}
	
	public void assignLeaders(CustomSubgraph subgraph, int timestep, int community){
		//asserted
		List<Node> graphNodes = OverlapCommunityDetection.getCommunityNodes(timestep, community);
		
		NodeFeatures nodefeatures = new NodeFeatures();
		List<Double> eigenvector = nodefeatures.eigenvectorcentralityLeadershipCalculation(subgraph);
		int leadersCount=0;
		//TODO: strong left skewness and a peak near the maximum for mean+sd not feasible
		leadershipThreshold = GlobalVariables.leaderThreshold;
		
		boolean flag = false;
		int j=0;
		for (Node node : subgraph.vertexSet()) {
			for (int i = 0; i < graphNodes.size() && !flag ; i++) {
            	if(node.getId() == graphNodes.get(i).getId()){
            		
        			graphNodes.get(i).setEigenCentrality(eigenvector.get(j));
                	if(eigenvector.get(j) > leadershipThreshold){
                		graphNodes.get(i).setAsLeader();
                		leadersCount++;
                	}
            		j++;
                    flag=true;
                }
            }
            flag=false;
		}
		OverlapCommunityDetection.Communities.get(timestep)[community].setNumLeaders(leadersCount);
	}
}
