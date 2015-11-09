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
	
	//threshold for detecting leaders
	private double leadershipThreshold;
	
	//constructor
	public LeadershipFeatures() {

	}
	
	/**
	 * assigning leader based on the computed eigenvector centrality values for each node
	 * @param subgraph
	 * @param timestep
	 * @param community
	 */
	public void assignLeaders(CustomSubgraph subgraph, int timestep, int community){
		
		//list of nodes from the community structure
		List<Node> graphNodes = OverlapCommunityDetection.getCommunityNodes(timestep, community);
		
		//compute eigenvector centrality
		NodeFeatures nodefeatures = new NodeFeatures();
		List<Double> eigenvector = nodefeatures.eigenvectorcentralityLeadershipCalculation(subgraph);

		int leadersCount=0;
		boolean flag = false;
		int j=0;
		leadershipThreshold = GlobalVariables.leaderThreshold;

		//assign node as leader if the centrality is greater than the threshold
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
