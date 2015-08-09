package main.sg.javapackage.sna.features;

import java.util.List;

import main.sg.javapackage.domain.CustomSubgraph;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.ocd.OverlapCommunityDetection;

public class LeadershipFeatures {
	
	private double leadershipThreshold;
	public LeadershipFeatures() {
		// TODO Auto-generated constructor stub
	}
	
	public void assignLeaders(CustomSubgraph subgraph, int timestep, int community){

		List<Node> graphNodes = OverlapCommunityDetection.getCommunityNodes(timestep, community);
		
		NodeFeatures nodefeatures = new NodeFeatures();
		List<Double> eigenvector = nodefeatures.eigenvectorcentralityLeadershipCalculation(subgraph);
		//double mean = StatisticManager.getMean(eigenvector);
		//double sd = StatisticManager.getStdDev(eigenvector);
		int leadersCount=0;
		//TODO: strong left skewness and a peak near the maximum for mean+sd
		//leadershipThreshold = mean+sd;
		leadershipThreshold = 0.9;
		
		boolean flag = false;
		int j=0;
		for (Node node : subgraph.vertexSet()) {
			
            //TODO:CHECK THIS CHECK THIS ->copy value to the right node
            for (int i = 0; i < graphNodes.size() && !flag ; i++) {
            	if(node.getId() == graphNodes.get(i).getId()){
            		
        			graphNodes.get(i).setEigenCentrality(eigenvector.get(j));
        			//TODO:leader threshold
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
