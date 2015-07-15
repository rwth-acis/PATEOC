package main.sg.javapackage.sna;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import main.sg.javapackage.domain.CustomGraph;
import main.sg.javapackage.domain.CustomSubgraph;
import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.ocd.OverlapCommunityDetection;


public class SocialNetworkAnalysis {
	
	private CustomGraph graph;
	private CustomSubgraph subgraph;
	
	public SocialNetworkAnalysis() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * function to form subgraphs of each community and extract
	 * the node,leadership,community level features.
	 * the features are stored in the community MAP
	 * 
	 */
	public void extractAnalytics(){
		
		System.out.println("Running statistical extraction ...");
		Logger.writeToLogln("Details extracted include ...");
		Logger.writeToLogln("NodeSize, EdgeSize, N_Leaders, SizeRatio, LeaderRatio, Density, Cohesion, ClusterCoefficient, Assortativity, "
				+ "DegreeCentrality, ClosenessCentrality ");
		int timestep,community;
		
		for(timestep = 1;timestep <= PreProcessing.totalGraphCount() ; timestep++) {
			
			Logger.writeToLogln("");
			Logger.writeToLogln("Graph "+ timestep +" statistical values -");
			
			graph = PreProcessing.getParticularGraph(timestep);
			
			for(community = 1; community<= OverlapCommunityDetection.numOfCommunities(timestep) ; community++) {
				
				List<Node> graphNodes = OverlapCommunityDetection.getCommunityNodes(timestep, community);
				Set<Node> tempNodeSet = new LinkedHashSet<Node>(reassertNodes(graphNodes));
				
				subgraph = new CustomSubgraph(graph, tempNodeSet, null);
				subgraphChecksum(timestep, community);
				//main.sg.javapackage.ext.graphml.CustomGraphMLExporter.GraphMLExportSubgraph(subgraph, 100*timestep + community);
				
				LeadershipFeatures features1 = new LeadershipFeatures();
				features1.assignLeaders(subgraph,timestep, community);
				
				CommunityFeatures features2 = new CommunityFeatures(graph, subgraph);
				
				OverlapCommunityDetection.Communities.get(timestep)[community].setAttrSizeRatio(features2.calculateSizeRatio());
				OverlapCommunityDetection.Communities.get(timestep)[community].setAttrLeaderRatio(/*domain_calculation*/);
				OverlapCommunityDetection.Communities.get(timestep)[community].setAttrDensity(features2.calculateDensity());
				double cohesionValue = features2.calculateCohesion();
				if(Double.isFinite(cohesionValue)){
					OverlapCommunityDetection.Communities.get(timestep)[community].setAttrCohesion(cohesionValue);
				}
				else{
					OverlapCommunityDetection.Communities.get(timestep)[community].setAttrCohesion(GlobalVariables.COHESION_INFINITY);
				}
				OverlapCommunityDetection.Communities.get(timestep)[community].setAttrClusteringCoefficient(features2.calculateClusteringCoefficient());
				OverlapCommunityDetection.Communities.get(timestep)[community].setAttrAssortativity(features2.calculateAssoratitivity(graphNodes));
				OverlapCommunityDetection.Communities.get(timestep)[community].setAttrDegreeCentrality(features2.calculateDegreeCentrality());
				OverlapCommunityDetection.Communities.get(timestep)[community].setAttrClosenessCentrality(features2.calculateClosenessCentrality());
				
				
				Logger.writeToLogln(community+
						"- "+subgraph.vertexSet().size()+
						","+ subgraph.edgeSet().size()+
						","+ OverlapCommunityDetection.Communities.get(timestep)[community].getNumLeaders()+
						","+ OverlapCommunityDetection.Communities.get(timestep)[community].getAttrSizeRatio()+
						","+ OverlapCommunityDetection.Communities.get(timestep)[community].getAttrLeaderRatio()+
						","+ OverlapCommunityDetection.Communities.get(timestep)[community].getAttrDensity()+
						","+ OverlapCommunityDetection.Communities.get(timestep)[community].getAttrCohesion()+
						","+ OverlapCommunityDetection.Communities.get(timestep)[community].getAttrClusteringCoefficient()+
						","+ OverlapCommunityDetection.Communities.get(timestep)[community].getAttrAssortativity()+
						","+ OverlapCommunityDetection.Communities.get(timestep)[community].getAttrDegreeCentrality()+
						","+ OverlapCommunityDetection.Communities.get(timestep)[community].getAttrClosenessCentrality());
				
				
			}
			System.out.println("Graph " + timestep + ": processed");
		}
	}
	
	/**
	 * returns the corresponding main nodes from the master list 
	 * for each copy node of the community; to create subgraphs
	 * 
	 * @param nodeSet - temporary copies of the nodes in Community MAP
	 * @return list - masterlist of nodes
	 */
	private List<Node> reassertNodes(List<Node> nodeSet) {
	//Return master nodes for the corresponding node copies from the communities
		List<Node> newNodeSet = new ArrayList<Node>();
		for (int i = 0; i < nodeSet.size(); i++) {
			newNodeSet.add( PreProcessing.masterlistGetNode( (nodeSet.get(i).getLabel().toString()) ) );
		}
		
		return newNodeSet;
	}
	
	/**
	 * temporary subgraph checksum;
	 * checks number of nodes in the induced subgraph to 
	 * number of nodes in Community MAP
	 * @param timestep
	 * @param community
	 */
	private void subgraphChecksum(int timestep, int community){
		
		if(subgraph.vertexSet().size() != OverlapCommunityDetection.Communities.get(timestep)[community].nodelistSize()){
			System.out.println("CHECKSUM FAILED FOR SUBGRAPH");
		}
		
	}

}
