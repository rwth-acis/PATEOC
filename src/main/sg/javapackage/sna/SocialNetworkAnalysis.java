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
import main.sg.javapackage.metrics.DegreeDistribution;
import main.sg.javapackage.ocd.OverlapCommunityDetection;
import main.sg.javapackage.sna.features.CommunityFeatures;
import main.sg.javapackage.sna.features.LeadershipFeatures;


/**
 * Primary class for feature aggregation of all 
 * detected communities from all timestep of input graph
 * @author Stephen
 *
 */
public class SocialNetworkAnalysis {
	
	//graph
	private CustomGraph graph;
	
	//subgraph
	private CustomSubgraph subgraph;
	
	//flag to extract subgraph
	private boolean subgraphExtract = GlobalVariables.subgraphExtract;
	
	//pointer to results file
	private static String resultFile = GlobalVariables.resultFile;
	
	//constructor
	public SocialNetworkAnalysis() {
		System.out.println("Running SNA Extractor ..");
		Logger.writeToFile(resultFile,"OUTPUT FILE FOR RESULTS\n\n",false);
	}
	
	/**
	 * function to form subgraphs of each community and extract
	 * the node,leadership,community level features.
	 * the features are stored in the community MAP
	 * 
	 */
	public void extractAnalytics(){
		
		//minimum community size
		int commSize = GlobalVariables.communitySizeThreshold;
		
		//local variables
		int timestep,community;
		
		Logger.writeToLogln("Details extracted include ...");
		Logger.writeToLogln("NodeSize, EdgeSize, N_Leaders, SizeRatio, LeaderRatio, Density, Cohesion, ClusterCoefficient, "
				+ "SpearmanRho, DegreeCentrality, ClosenessCentrality, EigenVectorCentrality, "
				+ "LDegreeCentrality, LClosenessCentrality, LEigenVectorCentrality ");
		
		
		//initializing the distribution class for each input graph
		DegreeDistribution powerlaw = new DegreeDistribution();
		
		//for each graph 1 to N-1
		for(timestep = 1;timestep <= PreProcessing.totalGraphCount() ; timestep++) {
			
			Logger.writeToLogln("");
			Logger.writeToLogln("Graph "+ timestep +" statistical values -");
			
			//retrieve each graph
			graph = PreProcessing.getParticularGraph(timestep);
			
			//aggregate the available degrees from the graph
			powerlaw.updateDegreeFrequency(graph);
			
			//for each community in the graph
			for(community = 1; community<= OverlapCommunityDetection.numOfCommunities(timestep) ; community++) {
				
				//retrieve the set of nodes from the master list
				List<Node> graphNodes = OverlapCommunityDetection.getCommunityNodes(timestep, community);
				Set<Node> tempNodeSet = new LinkedHashSet<Node>(reassertNodes(graphNodes));
				
				//convert community into subgraph
				subgraph = new CustomSubgraph(graph, tempNodeSet, null);
				
				//flag to extract subgraph
				if(subgraphExtract){
					CustomGraphMLExporter.GraphMLExportSubgraph(subgraph, 100*timestep + community);
				}
				
				//subgraph check
				if(subgraph.vertexSet().size() < commSize || subgraph.edgeSet().size()< 2 ){
					Logger.writeToLogln(community+" Skipped due to < Threshold nodes || < Disjoint graph");
					continue;
				}
				
				//calculate all features of the community
				//Node-Level
				LeadershipFeatures features1 = new LeadershipFeatures();
				features1.assignLeaders(subgraph,timestep, community);
				
				//Community-Level
				CommunityFeatures features2 = new CommunityFeatures(graph, subgraph);
				Community C_i_P = OverlapCommunityDetection.Communities.get(timestep)[community];
				C_i_P.setNumNodes(subgraph.vertexSet().size());
				C_i_P.setNumEdges(subgraph.edgeSet().size());
				C_i_P.setAttrSizeRatio(features2.calculateSizeRatio());
				C_i_P.setAttrLeaderRatio(/*domain_calculation*/);
				C_i_P.setAttrDensity(features2.calculateDensity());
				C_i_P.setAttrCohesion(features2.calculateCohesion());
				C_i_P.setAttrClusteringCoefficient(features2.calculateClusteringCoefficient());
				C_i_P.setAttrSpearmanRho(features2.calculateSpearmanMeasure());
				C_i_P.setAttrDegreeCentrality(features2.calculateDegreeCentrality(graphNodes));
				C_i_P.setAttrClosenessCentrality(features2.calculateClosenessCentrality(graphNodes));
				C_i_P.setAttrEigenVectorCentrality(features2.calculateEigenVectorCentrality(graphNodes));
				C_i_P.setAttrLeaderDegreeCentrality(features2.calculateLeaderDegreeCentrality(graphNodes));
				C_i_P.setAttrLeaderClosenessCentrality(features2.calculateLeaderClosenessCentrality(graphNodes));
				C_i_P.setAttrLeaderEigenVectorCentrality(features2.calculateLeaderEigenVectorCentrality(graphNodes));
				
				//Printer
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
		//compute power law for all the graphs together
		powerlaw.computeDegreeDistribution();

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
