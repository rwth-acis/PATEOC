package main.sg.javapackage.sna;

import java.util.List;

import main.sg.javapackage.domain.CustomSubgraph;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.ocd.OverlapCommunityDetection;
import main.sg.javapackage.utils.StatisticManager;

public class LeadershipFeatures {
	
	private double leadershipThreshold;
	public LeadershipFeatures() {
		// TODO Auto-generated constructor stub
	}
	
	public void assignLeaders(CustomSubgraph subgraph, int timestep, int community){

		List<Node> graphNodes = OverlapCommunityDetection.getCommunityNodes(timestep, community);
		
		NodeFeatures nodefeatures = new NodeFeatures();
		List<Double> assortativityvector = nodefeatures.disassortativityLeadershipCalculation(subgraph);
		List<Double> eigenvector = nodefeatures.eigenvectorcentralityLeadershipCalculation(subgraph);
		double mean = StatisticManager.getMean(eigenvector);
		double sd = StatisticManager.getStdDev(eigenvector);
		
		int leadersCount=0;
		leadershipThreshold = mean+sd;
		for(int i = 0; i< graphNodes.size(); i++){
			Node node = graphNodes.get(i);
			node.setAssortativityValue(assortativityvector.get(i));
			node.setEigenCentrality(eigenvector.get(i));
			//TODO:leader threshold
        	if(eigenvector.get(i) > leadershipThreshold){
        		node.setAsLeader();
        		leadersCount++;
        	}
		}
		OverlapCommunityDetection.Communities.get(timestep)[community].setNumLeaders(leadersCount);
		//System.out.println("Nodes in community= "+graphNodes.size() + " & Leaders= "+ leadersCount);
	}

}
