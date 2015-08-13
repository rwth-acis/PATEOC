package main.sg.javapackage.sna;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import main.sg.javapackage.domain.Community;
import main.sg.javapackage.domain.CustomGraph;
import main.sg.javapackage.domain.CustomSubgraph;
import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.ext.graphml.CustomGraphMLExporter;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.ocd.OverlapCommunityDetection;
import main.sg.javapackage.sna.features.CommunityFeatures;
import main.sg.javapackage.sna.features.LeadershipFeatures;


public class SocialNetworkAnalysis {
	
	private CustomGraph graph;
	private CustomSubgraph subgraph;
	private boolean subgraphExtract = GlobalVariables.subgraphExtract;
	
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
		Logger.writeToLogln("NodeSize, EdgeSize, N_Leaders, SizeRatio, LeaderRatio, Density, Cohesion, ClusterCoefficient, "
				+ "SpearmanRho, DegreeCentrality, ClosenessCentrality, EigenVectorCentrality, "
				+ "LDegreeCentrality, LClosenessCentrality, LEigenVectorCentrality ");
		
		int timestep,community;
		
		for(timestep = 1;timestep <= PreProcessing.totalGraphCount() ; timestep++) {
			
			Logger.writeToLogln("");
			Logger.writeToLogln("Graph "+ timestep +" statistical values -");
			
			graph = PreProcessing.getParticularGraph(timestep);
			for(community = 1; community<= OverlapCommunityDetection.numOfCommunities(timestep) ; community++) {
				
				List<Node> graphNodes = OverlapCommunityDetection.getCommunityNodes(timestep, community);
				Set<Node> tempNodeSet = new LinkedHashSet<Node>(reassertNodes(graphNodes));
				subgraph = new CustomSubgraph(graph, tempNodeSet, null);
				if(subgraphExtract){
					CustomGraphMLExporter.GraphMLExportSubgraph(subgraph, 100*timestep + community);
				}
				
				if(subgraph.vertexSet().size() <=3 || subgraph.edgeSet().size()<=1 ){
					Logger.writeToLogln(community+" Skipped due to <= 3 nodes || <=1 edges ");
					continue;
				}
				
				LeadershipFeatures features1 = new LeadershipFeatures();
				features1.assignLeaders(subgraph,timestep, community);
				CommunityFeatures features2 = new CommunityFeatures(graph, subgraph);
				Community C_i_P = OverlapCommunityDetection.Communities.get(timestep)[community];
				C_i_P.setNumNodes(subgraph.vertexSet().size());
				C_i_P.setNumEdges(subgraph.edgeSet().size());
				C_i_P.setAttrSizeRatio(features2.calculateSizeRatio());
				C_i_P.setAttrLeaderRatio(/*domain_calculation*/);
				C_i_P.setAttrDensity(features2.calculateDensity());
				double cohesionValue = features2.calculateCohesion();
				if(Double.isFinite(cohesionValue)){
					C_i_P.setAttrCohesion(cohesionValue);
				}
				else{
					C_i_P.setAttrCohesion(GlobalVariables.COHESION_INFINITY);
				}
				C_i_P.setAttrClusteringCoefficient(features2.calculateClusteringCoefficient());
				C_i_P.setAttrSpearmanRho(features2.calculateSpearmanMeasure());
				C_i_P.setAttrDegreeCentrality(features2.calculateDegreeCentrality(graphNodes));
				C_i_P.setAttrClosenessCentrality(features2.calculateClosenessCentrality(graphNodes));
				C_i_P.setAttrEigenVectorCentrality(features2.calculateEigenVectorCentrality(graphNodes));
				C_i_P.setAttrLeaderDegreeCentrality(features2.calculateLeaderDegreeCentrality(graphNodes));
				C_i_P.setAttrLeaderClosenessCentrality(features2.calculateLeaderClosenessCentrality(graphNodes));
				C_i_P.setAttrLeaderEigenVectorCentrality(features2.calculateLeaderEigenVectorCentrality(graphNodes));
				
				Logger.writeToLogln(community+
						"- "+C_i_P.getNumNodes()+
						","+ C_i_P.getNumEdges()+ 
						","+ C_i_P.getNumLeaders()+
						","+ C_i_P.getAttrSizeRatio()+
						","+ C_i_P.getAttrLeaderRatio()+
						","+ C_i_P.getAttrDensity()+
						","+ C_i_P.getAttrCohesion()+
						","+ C_i_P.getAttrClusteringCoefficient()+
						","+ C_i_P.getAttrSpearmanRho()+
						","+ C_i_P.getAttrDegreeCentrality()+
						","+ C_i_P.getAttrClosenessCentrality()+
						","+ C_i_P.getAttrEigenVectorCentrality()+
						","+ C_i_P.getAttrLeaderDegreeCentrality()+
						","+ C_i_P.getAttrLeaderClosenessCentrality()+
						","+ C_i_P.getAttrLeaderEigenVectorCentrality());				
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

}
