package main.sg.javapackage.sna.features;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.sg.javapackage.domain.CustomGraph;
import main.sg.javapackage.domain.CustomSubgraph;
import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.domain.Node;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.RankingAlgorithm;
import org.apache.commons.math3.stat.ranking.TiesStrategy;
import org.jgrapht.Graphs;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Support class for feature extraction
 * -Community Level
 * @author Stephen
 *
 */
public class CommunityFeatures {
	
	//graph
	private CustomGraph graph;
	
	//subgraph
	private CustomSubgraph subgraph;
	
	//constructor
	public CommunityFeatures(CustomGraph graph, CustomSubgraph subgraph) {
		this.graph = graph;
		this.subgraph = subgraph;
	}
	
	/**
	 * compute community's ratio of size with respect to the entire graph
	 * 
	 */
	public double calculateSizeRatio(){
	//Asserted
		
		double sizeRatio = Double.NaN;
		sizeRatio = (double)subgraph.vertexSet().size()/(double)graph.vertexSet().size();

        //System.out.println("calculateSizeRatio :" + (double) sizeRatio);
		return sizeRatio;
	}
	
	/**
	 * compute community's edge density value 
	 * 
	 */
	public double calculateDensity() {
	//Asserted
		
		long numVert = subgraph.vertexSet().size();
		long maxNumEdge = numVert*(numVert - 1);
		long numEdge = 0;
		double density = Double.NaN;
		Set<Node> vertices = subgraph.vertexSet();
		for (Node from : vertices){
			for (Node to : vertices){
				if (from == to) continue;
				if (graph.containsEdge(from, to)) numEdge++;
			}
		}
		density = (double)numEdge /(double)maxNumEdge;
        //System.out.println("calculateDensity :" + (double)density);
		return density;
	}
	
	/**
	 * compute community's cohesion value 
	 * 
	 */
	public double calculateCohesion(){
    //Cohesion, Non-asserted
		long numVert = subgraph.vertexSet().size();
		long graphNumVert = graph.vertexSet().size();
    	double cohesion = Double.NaN;
    	long outerEdges = 0;

    	//long maxNumEdge = numVert * (graph.vertexSet().size() - numVert);
    	long maxNumEdge = graphNumVert * (graphNumVert - numVert);

		Set<Node> vertices = subgraph.vertexSet();
		for (Node node : vertices){
			outerEdges += (graph.edgesOf(node).size() - subgraph.edgesOf(node).size());
		}
		double density = calculateDensity();
		cohesion = density / ((double)outerEdges / (double)maxNumEdge);
		
		if(Double.isInfinite(cohesion)){
			cohesion = GlobalVariables.COHESION_INFINITY;
		}
		if(GlobalVariables.normalizeFeatures){
			cohesion = normalize(cohesion, 0, GlobalVariables.COHESION_INFINITY, 0, 1);
		}
    	return cohesion;
    }
	
	/**
	 * compute community's clustering coefficient value 
	 * 
	 */
	public double calculateClusteringCoefficient() {
    //Asserted 

        Map<Node, Double> nodesMap_CC = new HashMap<Node, Double>();
        double avgCCoeff = 0.0;

        for (Node node : subgraph.vertexSet()) {

            List<Node> neighbors = Graphs.neighborListOf(subgraph, node);
            int n_neighborspairs = 0;

            if (neighbors.size() < 2) {
                nodesMap_CC.put(node, 0.0);
                continue;
            }

            for (int i = 0; i < neighbors.size(); i++) {
                for (int j = i + 1; j < neighbors.size(); j++) {
                    if (subgraph.containsEdge(neighbors.get(i), neighbors.get(j)))
                        n_neighborspairs++;
                }
            }
            double C_i = (2.0 * n_neighborspairs) / (neighbors.size() * (neighbors.size() - 1.0));
            nodesMap_CC.put(node, C_i);
        }
        for (Map.Entry<Node, Double> entry : nodesMap_CC.entrySet())
        {
            avgCCoeff += entry.getValue();     
        }
        avgCCoeff = avgCCoeff / nodesMap_CC.size();
        //System.out.println("Value :" + (double) avgCCoeff);
        return avgCCoeff;
    }
	
	/**
	 * compute community's degree centrality value 
	 * 
	 */
    public double calculateDegreeCentrality(List<Node> graphNodes) {
    	//Asserted
        Map<Node, Double> nodesMap_DegCen = new HashMap<Node, Double>();
        double degCentrality = 0.0;

        int numVert_1 = subgraph.vertexSet().size() - 1;
        boolean flag = false;
        for (Node node : subgraph.vertexSet()) {
        	degCentrality = (double) subgraph.degreeOf(node) / (double) numVert_1;
            nodesMap_DegCen.put(node, degCentrality);
            
            for (int i = 0; i < graphNodes.size() && !flag ; i++) {
            	if(graphNodes.get(i).getId() == node.getId()){
                    graphNodes.get(i).setDegreeCentrality(degCentrality);
                    flag=true;
                }
            }
            flag=false;
        }

		degCentrality = 0.0;

		for (Map.Entry<Node, Double> entry : nodesMap_DegCen.entrySet())
        {
        	degCentrality += entry.getValue();          
        }
        degCentrality = degCentrality / (double) nodesMap_DegCen.size();
        //System.out.println("Value :" + (double) degCentrality);
        return degCentrality;
    }
	
	/**
	 * compute community's degree centrality value of its leader nodes
	 * 
	 */
    public double calculateLeaderDegreeCentrality(List<Node> graphNodes){
    	//Asserted
        int leadercount=0;
        double degCentrality = 0.0;

    	for (Node node : graphNodes) {
			if(node.getIsLeader()){
				degCentrality += node.getDegreeCentrality();
				leadercount++;
			}
		}
        degCentrality = degCentrality / (double) leadercount;
        //System.out.println("Value :" + (double) degCentrality);
        return degCentrality;
    	
    }

	/**
	 * compute community's closeness centrality value 
	 * 
	 */
	public double calculateClosenessCentrality(List<Node> graphNodes) {
	//ClosenessCentrality, Asserted
		
		Map<Node, Double> nodesMap_CC = new HashMap<Node, Double>();
		double closenessCentrality = 0.0;
		int numVert_1 = subgraph.vertexSet().size() - 1;
        boolean flag = false;

		FloydWarshallShortestPaths<Node, DefaultWeightedEdge> fws = new FloydWarshallShortestPaths<Node, DefaultWeightedEdge>(subgraph);
		for (Node u : subgraph.vertexSet()) {
			
			fws.getShortestPaths(u);
			double sum = 0.0;
			for (Node v : subgraph.vertexSet()) {
				if(u!=v){
					Double length = fws.shortestDistance(u, v);
					if(length == null){
						length = Double.POSITIVE_INFINITY;
					}
					sum+=length;
					if (Double.isInfinite(sum)) 
						break;
				}
                
			}
            nodesMap_CC.put(u, (double) (numVert_1) / sum);
            
            for (int i = 0; i < graphNodes.size() && !flag ; i++) {
            	if(graphNodes.get(i).getId() == u.getId()){
                    graphNodes.get(i).setClosenessCentrality(nodesMap_CC.get(u));
                    flag=true;
                }
            }
            flag=false;
            
		}
		
		closenessCentrality = 0.0;

		for (Map.Entry<Node, Double> entry : nodesMap_CC.entrySet())
        {
        	closenessCentrality += entry.getValue();    
        }
        closenessCentrality = (double) closenessCentrality / (double) nodesMap_CC.size();
        //System.out.println("Closeness: "+ closenessCentrality);
        return closenessCentrality;
	}

	/**
	 * compute community's clustering coefficient value of its leader nodes
	 * 
	 */
	public double calculateLeaderClosenessCentrality(List<Node> graphNodes){
		
		double closenessCentrality = 0.0;
		int leadercount=0;
		
		for (Node u : graphNodes) {
			if(u.getIsLeader()){
				closenessCentrality += u.getClosenessCentrality();
				leadercount++;
			}
		}
        closenessCentrality = closenessCentrality / (double) leadercount;
        //System.out.println("Closeness: "+ closenessCentrality);
        return closenessCentrality;
	}
	
	/**
	 * compute community's eigen vector centrality value 
	 * 
	 */
	public double calculateEigenVectorCentrality(List<Node> graphNodes){
	//Asserted
		double eigenVectorCentrality = 0.0f;
		int numVert=graphNodes.size();
		
		for (Node node : graphNodes) {
			eigenVectorCentrality += node.getEigenCentrality();
		}
		
		eigenVectorCentrality = eigenVectorCentrality/(double) numVert ;
		return eigenVectorCentrality;
	}
	
	/**
	 * compute community's eigen vector centrality value of its leader nodes
	 * 
	 */
	public double calculateLeaderEigenVectorCentrality(List<Node> graphNodes){
		//Asserted
		int leadercount=0;
		double eigenVectorCentrality = 0.0;
		for (Node node : graphNodes) {
			if(node.getIsLeader()){
				eigenVectorCentrality += node.getEigenCentrality();
				leadercount++;
			}
		}
		eigenVectorCentrality = eigenVectorCentrality/(double) leadercount ;
		return eigenVectorCentrality;
	}
	/**
	 * Based on the implementation from
	 *  https://github.com/Rofti/DMID/blob/5c287e32dcbc8152ae03f105a2cdf69b25bc76f2/Metrics/src/ocd/metrics/Main.java
	 *  Special thanks to MarvenVD - https://github.com/Rofti
	 * @return
	 */
	public double calculateSpearmanMeasure(){
		//Asserted
		double[] dataX = new double[subgraph.edgeSet().size()];
		double[] dataY = new double[subgraph.edgeSet().size()];

		DefaultWeightedEdge[] edges = subgraph.edgeSet()
				.toArray(new DefaultWeightedEdge[subgraph.edgeSet().size()]);
		
		for (int i = 0; i < subgraph.edgeSet().size(); ++i) {
			if (Math.random() > (1 / 2d)) {
				dataX[i] = subgraph.degreeOf(subgraph.getEdgeSource(edges[i])) + Math.random();
				dataY[i] = subgraph.degreeOf(subgraph.getEdgeTarget(edges[i])) + Math.random();
			} else {
				dataX[i] = subgraph.degreeOf(subgraph.getEdgeTarget(edges[i])) + Math.random();
				dataY[i] = subgraph.degreeOf(subgraph.getEdgeSource(edges[i])) + Math.random();
			}
		}			

		RankingAlgorithm natural = new NaturalRanking(NaNStrategy.MAXIMAL,
				TiesStrategy.AVERAGE);
		SpearmansCorrelation spearmansCorr = new SpearmansCorrelation(natural);
		double spearmanRho = spearmansCorr.correlation(dataX, dataY);
		if(GlobalVariables.normalizeFeatures){
			spearmanRho = normalize(spearmanRho, -1, 1, 0, 1);
		}
		return spearmanRho;
	}
	
	/**
	 * map values from one range to another
	 * @param value
	 * @param in_min range1 min
	 * @param in_max range1 max
	 * @param out_min range2 min
	 * @param out_max range2 max
	 * @return
	 */
	private double normalize(double value, double in_min, double in_max, double out_min, double out_max)
	{
	  return (value - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

}
