package main.sg.javapackage.ocd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.sparse.CCSMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;
import org.la4j.vector.dense.BasicVector;

import main.sg.javapackage.domain.Community;
import main.sg.javapackage.domain.CustomGraph;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
/**
 * Implements a custom version of Disassortative Degree Mixing and Information Diffusion Algorithm 
 * for overlapping community detection
 * On weighted, undirected graphs.
 * 
 */
public class DMID {
	
	
	/**
	 * The iteration bound for the leadership calculation phase. The default
	 * value is 1000. Must be greater than 0.
	 */
	private int randomWalkIterationBound = 1000;
	/**
	 * The precision factor for the leadership calculation phase.
	 * The phase ends when the infinity norm of the difference between the updated vector and
	 * the previous one is smaller than this factor.
	 * The default value is 0.001. Must be greater than 0 and smaller than infinity.
	 * Recommended are values close to 0.
	 */
	private double randomWalkPrecisionFactor = 0.001;
	
	/**
	 * The profitability step size for the label propagation phase. The default
	 * value is 0.1.  Must be in (0, 1).
	 */
	private double profitabilityDelta = 0.1;
	
	/**
	 * DMID filter membership matrix threshold
	 */
	private double dmidMembershipMatrixThreshold = 0.25f;

	/**
	 * input graph
	 */
	private CustomGraph workingGraph;
	
	//constructor
	public DMID() {

	}
	
	public void runDMID(int timestep){
		//retrieve internal graph
		workingGraph = PreProcessing.getParticularGraph(timestep);
		
		//1st stage of DMID
		List<Node> leaders = randomWalkPhase();
		
		//2nd stage of DMID
		Matrix membershipMatrix = labelPropagationPhase(workingGraph, leaders);
		
		//store the communities in custom data structure
		parseCoverMatrix(workingGraph, membershipMatrix,timestep);
	}
	
	
	/**
	 * Executes the random walk phase of the algorithm and returns global
	 * leaders.
	 * 
	 * @param graph The graph whose leaders will be detected.
	 * 
	 * @return A list containing all nodes which are global leaders.
	 */
	protected List<Node> randomWalkPhase(){
		
		Matrix disassortativityMatrix = getTransposedDisassortativityMatrix(workingGraph);
		Vector disassortativityVector = executeRandomWalk(disassortativityMatrix);
		Vector leadershipVector = getLeadershipValues(workingGraph, disassortativityVector);
		Map<Node, Double> followerMap = getFollowerDegrees(workingGraph, leadershipVector);
		return getGlobalLeaders(followerMap);

	}

	/**
	 * Returns the transposed normalized disassortativity matrix for the random
	 * walk phase.
	 * 
	 * @param workingGraph The graph whose disassortativity matrix will be derived.
	 * 
	 * @return The transposed normalized disassortativity matrix.
	 */
	protected Matrix getTransposedDisassortativityMatrix(CustomGraph workingGraph){
		/**
		 * Calculates transposed disassortativity matrix in a special sparse
		 * matrix format.
		 */
		Matrix disassortativities = new CCSMatrix(workingGraph.vertexSet().size(),
				workingGraph.vertexSet().size());
		
		Set<DefaultWeightedEdge> edges = workingGraph.edgeSet();
		Set<Node> nodes = workingGraph.vertexSet();
		double disassortativity;	
		
		if(!edges.isEmpty()){
			for(DefaultWeightedEdge edge : edges){
				disassortativity = Math
						.abs(workingGraph.degreeOf((workingGraph.getEdgeTarget(edge)))
								- workingGraph.degreeOf(workingGraph.getEdgeSource(edge)));
				
				disassortativities.set( getIndex(nodes, workingGraph.getEdgeTarget(edge)),
						getIndex(nodes, workingGraph.getEdgeSource(edge)), disassortativity);
				//addition from the initial version to handle undirected graphs
				disassortativities.set( getIndex(nodes, workingGraph.getEdgeSource(edge)),
						getIndex(nodes, workingGraph.getEdgeTarget(edge)), disassortativity);
			}
		}

		/*
		 * Column normalizes transposed disassortativity matrix.
		 */
		double norm;
		Vector column;

		for (int i = 0; i < disassortativities.columns(); i++) {
			column = disassortativities.getColumn(i);
			norm = column.fold(Vectors.mkManhattanNormAccumulator());
			if (norm > 0) {
				disassortativities.setColumn(i, column.divide(norm));
			}
		}
		return disassortativities;
	}
	/**
	 * Executes the random walk for the random walk phase. The vector is
	 * initialized with a uniform distribution.
	 * 
	 * @param disassortativityMatrix The disassortativity matrix on which the
	 * random walk will be performed.
	 * 
	 * @return The resulting disassortativity vector.
	 */
	protected Vector executeRandomWalk(Matrix disassortativityMatrix) {
		
		Vector vec1 = new BasicVector(disassortativityMatrix.columns());
		for (int i = 0; i < vec1.length(); i++) {
			vec1.set(i, (double) 1.0 / vec1.length());
		}
		Vector vec2 = new BasicVector(vec1.length());
		for (int iteration = 0; vec1.subtract(vec2).fold(
				Vectors.mkInfinityNormAccumulator()) > randomWalkPrecisionFactor
				&& iteration < randomWalkIterationBound; iteration++) {

			vec2 = new BasicVector(vec1);
			vec1 = disassortativityMatrix.multiply(vec1);
		}

		return vec1;
	}

	/**
	 * Calculates the leadership values of all nodes for the random walk phase.
	 * 
	 * @param workingGraph The graph containing the nodes.
	 * 
	 * @param disassortativityVector The disassortativity vector calculated
	 * earlier in the random walk phase.
	 * 
	 * @return A vector containing the leadership value of each node in the
	 * entry given by the node index.
	 */
	protected Vector getLeadershipValues(CustomGraph workingGraph,Vector disassortativityVector) {
		
		Vector leadershipVector = new BasicVector(workingGraph.vertexSet().size());
		Set<Node> nodes = workingGraph.vertexSet();
		double leadershipValue;
		if (!nodes.isEmpty()) {

			for(Node node : nodes){
				/*
				 * Note: degree normalization is left out since it
				 * does not influence the outcome.
				 */
				leadershipValue = workingGraph.degreeOf(node)
						* disassortativityVector.get(getIndex(nodes, node));
				leadershipVector.set(getIndex(nodes, node), leadershipValue);
				
			}

		}
		return leadershipVector;
	}
	
	/**
	 * Returns the index of the node in the list for matrix operations
	 * 
	 * @param nodes - list of nodes
	 * @param value - nodevalue
	 * 
	 * @return Integer
	 */
	public static int getIndex(Set<Node> nodes, Object value) {
	   int result = 0;
	   for (Object entry:nodes) {
	     if (entry.equals(value)) return result;
	     result++;
	   }
	   return -1;
	}
	
	/**
	 * Returns the follower degree of each node for the random walk phase.
	 * 
	 * @param graph The graph containing the nodes.
	 * 
	 * @param leadershipVector The leadership vector previous calculated during
	 * the random walk phase.
	 * 
	 * @return A mapping from the nodes to the corresponding follower degrees.
	 */
	protected Map<Node, Double> getFollowerDegrees(CustomGraph  workingGraph,
			Vector leadershipVector){
		Map<Node, Double> followerMap = new HashMap<Node, Double>();
		Set<Node> nodes = workingGraph.vertexSet();
		/*
		 * Iterates over all nodes to detect their local leader
		 */
		Set<DefaultWeightedEdge> edgesFromNode;
		double maxInfluence;
		List<Node> leaders = new ArrayList<Node>();
		Node successor;
		DefaultWeightedEdge successorEdge;
		double successorInfluence = 0;
		DefaultWeightedEdge nodeEdge;
		double followerDegree;
		
		for(Node node : nodes){
			
			edgesFromNode = workingGraph.edgesOf(node);
			maxInfluence = Double.NEGATIVE_INFINITY;
			leaders.clear();
			/*
			 * Checks all successors for possible leader
			 */
			for(DefaultWeightedEdge edge : edgesFromNode){
				successor = workingGraph.getEdgeTarget(edge);
				successorEdge = edge; 

				successorInfluence = leadershipVector.get((int) (getIndex(nodes, successor))) * 
						workingGraph.getEdgeWeight(successorEdge);
				if (successorInfluence >= maxInfluence) {
					nodeEdge = workingGraph.getEdge(successor, node);
					/*
					 * Ensures the node itself is not a leader of the successor
					 */
					if (nodeEdge == null
							|| successorInfluence > leadershipVector.get((int) getIndex(nodes, node)) *
									workingGraph.getEdgeWeight(nodeEdge)) {
						if (successorInfluence > maxInfluence) {
							/*
							 * Other nodes have lower influence
							 */
							leaders.clear();
						}
						leaders.add(successor);
						maxInfluence = successorInfluence;
					}
				}
			}
			if (!leaders.isEmpty()) {
				for (Node leader : leaders) {
					followerDegree = 0;
					if (followerMap.containsKey(leader)) {
						followerDegree = followerMap.get(leader);
					}
					followerMap.put(leader,
							followerDegree += 1d / leaders.size());
				}
			}
		}
		return followerMap;
	}

	/**
	 * Returns a list of global leaders for the random walk phase.
	 * 
	 * @param followerMap The mapping from nodes to their follower degrees
	 * previously calculated in the random walk phase.
	 * 
	 * @return A list containing all nodes which are considered to be global
	 * leaders.
	 */
	protected List<Node> getGlobalLeaders(Map<Node, Double> followerMap) {
		double averageFollowerDegree = 0;
		for (Double followerDegree : followerMap.values()) {
			
			averageFollowerDegree += followerDegree;
		}
		averageFollowerDegree /= followerMap.size();
		List<Node> globalLeaders = new ArrayList<Node>();
		for (Map.Entry<Node, Double> entry : followerMap.entrySet()) {

			if (entry.getValue() >= averageFollowerDegree) {
				globalLeaders.add(entry.getKey());
			}
		}
		return globalLeaders;
	}
	
	/**
	 * Executes the label propagation phase.
	 * 
	 * @param graph The graph which is being analyzed.
	 * 
	 * @param leaders The list of global leader nodes detected during the random
	 * walk phase.
	 * 
	 * @return A cover containing the detected communities.
	 */
	protected Matrix labelPropagationPhase(CustomGraph  workingGraph, List<Node> leaders){
		/*
		 * Executes the label propagation until all nodes are assigned to at
		 * least one community
		 */
		int iterationCount = 0;
		Map<Node, Map<Node, Integer>> tmpCommunities = new HashMap<Node, Map<Node, Integer>>();
		Map<Node, Integer> communityMemberships;
		do {
			tmpCommunities.clear();
			iterationCount++;
			for (Node leader : leaders) {
				communityMemberships = executeLabelPropagation(workingGraph, leader, 1
						- iterationCount * profitabilityDelta);
				tmpCommunities.put(leader, communityMemberships);
			}
		} while (1 - iterationCount * profitabilityDelta > 0
				&& !areAllNodesAssigned(workingGraph, tmpCommunities));
		return(getMembershipDegrees(workingGraph, tmpCommunities));

	}

	/**
	 * Executes the label propagation for a single leader to identify its
	 * community members.
	 * 
	 * @param graph The graph which is being analyzed.
	 * 
	 * @param leader The leader node whose community members will be identified.
	 * 
	 * @param profitabilityThreshold The threshold value that determines whether
	 * it is profitable for a node to join the community of the leader / assume
	 * its behavior.
	 * 
	 * @return A mapping containing the iteration count for each node that is a
	 * community member. The iteration count indicates, in which iteration the
	 * corresponding node has joint the community.
	 */
	protected Map<Node, Integer> executeLabelPropagation(CustomGraph  workingGraph,
			Node leader, double profitabilityThreshold) {
		Map<Node, Integer> memberships = new HashMap<Node, Integer>();
		int previousMemberCount;
		int iterationCount = 0;
		/*
		 * Iterates as long as new members assume the behavior.
		 */
		Set<Node> predecessors;
		Iterator<Node> nodeIt;
		Node node;
		double profitability;
		Set<DefaultWeightedEdge> edgesFromNode;
		Node nodeSuccessor;
		
		do {
			iterationCount++;
			previousMemberCount = memberships.size();
			predecessors = getBehaviorPredecessors(workingGraph, memberships, leader);
			nodeIt = predecessors.iterator();
			/*
			 * Checks for each predecessor of the leader behavior nodes whether
			 * it assumes the new behavior.
			 */
			while (nodeIt.hasNext()) {
				node = nodeIt.next();
				profitability = 0;
				edgesFromNode = workingGraph.edgesOf(node);
				
				for(DefaultWeightedEdge edge : edgesFromNode){
					nodeSuccessor = workingGraph.getEdgeTarget(edge);
					Integer joinIteration = memberships.get(nodeSuccessor);
					if (nodeSuccessor.equals(leader) || 
							( joinIteration != null && joinIteration < iterationCount)) {
						profitability++;
					}
				}
				if (profitability / (double) edgesFromNode.size() > profitabilityThreshold) {
					memberships.put(node, iterationCount);
				}
			}
		} while (memberships.size() > previousMemberCount);
		return memberships;
	}

	/**
	 * Returns all predecessors of the nodes which adopted the leader's behavior
	 * (and the leader itself) for the label propagation of each leader.
	 * 
	 * @param graph The graph which is being analyzed.
	 * 
	 * @param memberships The nodes which have adopted leader behavior. Note
	 * that the membership degrees are not examined, any key value is considered
	 * a node with leader behavior.
	 * 
	 * @param leader The node which is leader of the community currently under
	 * examination.
	 * 
	 * @return A set containing all nodes that have not yet assumed leader
	 * behavior, but are predecessors of a node with leader behavior.
	 */
	protected Set<Node> getBehaviorPredecessors(CustomGraph  workingGraph,
			Map<Node, Integer> memberships, Node leader) {
		Set<Node> neighbors = new HashSet<Node>();

		Set<DefaultWeightedEdge> edgesToleader = workingGraph.edgesOf(leader);
		
		Node leaderPredecessor;
		for(DefaultWeightedEdge edge : edgesToleader) {
			leaderPredecessor = workingGraph.getEdgeSource(edge);
			if (!memberships.containsKey(leaderPredecessor)) {
				neighbors.add(leaderPredecessor);
			}
		}
		Set<DefaultWeightedEdge> edgesToMember;
		Node memberPredecessor;
		for (Node member : memberships.keySet()) {

			edgesToMember = workingGraph.edgesOf(member);
			
			for(DefaultWeightedEdge edge : edgesToMember){
				memberPredecessor = workingGraph.getEdgeSource(edge);
				if (!memberPredecessor.equals(leader)
						&& !memberships.containsKey(memberPredecessor)) {
					neighbors.add(memberPredecessor);
				}
			}
		}
		return neighbors;
	}

	/**
	 * Indicates for the label propagation phase whether all nodes have been
	 * assigned to at least one community.
	 * 
	 * @param graph The graph which is being analyzed.
	 * 
	 * @param tempCommunities A mapping from the leader nodes to the membership
	 * degrees of that leaders community.
	 * 
	 * @return TRUE when each node has been assigned to at least one community,
	 * and FALSE otherwise.
	 */
	protected boolean areAllNodesAssigned(CustomGraph  workingGraph,
			Map<Node, Map<Node, Integer>> tempCommunities){
		boolean allNodesAreAssigned = true;
		Set<Node> nodes = workingGraph.vertexSet();
		boolean nodeIsAssigned;

		for(Node node : nodes){

			nodeIsAssigned = false;
			for (Map.Entry<Node, Map<Node, Integer>> entry : tempCommunities
					.entrySet()) {
				if (entry.getValue().containsKey(node)) {
					nodeIsAssigned = true;
					break;
				}
			}
			if (!nodeIsAssigned) {
				allNodesAreAssigned = false;
				break;
			}
		}
		return allNodesAreAssigned;
	}

	/**
	 * Returns a cover containing the membership degrees of all nodes.,
	 * calculated from
	 * 
	 * @param graph The graph which is being analyzed.
	 * 
	 * @param tempCommunities A mapping from the leader nodes to the iteration count
	 * mapping of their community members.
	 * 
	 * @return A cover containing each nodes membership degree
	 */
	protected Matrix getMembershipDegrees(CustomGraph workingGraph,
			Map<Node, Map<Node, Integer>> tempCommunities){
		
		Matrix membershipMatrix = new Basic2DMatrix(workingGraph.vertexSet().size(),
				tempCommunities.size());
		Set<Node> nodes = workingGraph.vertexSet();
		int communityIndex = 0;
		double membershipDegree;
		for (Node leader : tempCommunities.keySet()) {
			
			membershipMatrix.set(getIndex(nodes,leader), communityIndex, 1.0);
			
			for (Map.Entry<Node, Integer> entry : tempCommunities.get(leader)
					.entrySet()) {
				membershipDegree = 1.0 / Math.pow(entry.getValue(), 2);
				membershipMatrix.set(getIndex(nodes,entry.getKey()), communityIndex,
						membershipDegree);
			}
			communityIndex++;
		}
		
		return membershipMatrix;
	}
	
	/**
	 * store the detected community for each graph in the Community data structure
	 * @param workingGraph
	 * @param membershipMatrix
	 * @param timestep
	 */
	protected void parseCoverMatrix(CustomGraph workingGraph, Matrix membershipMatrix, int timestep){
		
		//set of vertices of the input graph
		Set<Node> nodes = workingGraph.vertexSet();
		
		//columns of membership matrix correspond to a community 
		//and values are belonging factor of each node to that community
		int numOfCommunities = membershipMatrix.columns();
		
		//reserve array of communities
		Community[] tempComm = new Community[numOfCommunities+1];
		for(int i=1; i<=numOfCommunities; i++ ){
			tempComm[i] = new Community();
			tempComm[i].setId((long)i);
		}
		
		//Set the 0th Node as header node with long id as number of communities
		tempComm[0] = new Community();
		tempComm[0].setId((long) numOfCommunities); 
		tempComm[0].setHeaderLabel();
		
		//process each node and add to corresponding community if the belonging factor is greater than the threshold
		for(Node node : nodes){
			for(int j=0; j<membershipMatrix.columns();j++){
				if(membershipMatrix.get(getIndex(nodes,node),j) > dmidMembershipMatrixThreshold){
					tempComm[j].addNode(node);
				}
			}
		}
		OverlapCommunityDetection.Communities.put(timestep, tempComm);
		
		//printer statements
		Logger.writeToLogln("Total number of communities :" + numOfCommunities);
		System.out.println("Graph "+timestep+ " has "+ numOfCommunities+" communities");
		for(int i=1; i<=numOfCommunities; i++ ){
			Logger.writeToLogln("	Community "+ i + " has "+ tempComm[i].getNodeList().size() + " nodes");
		}
	}
}
