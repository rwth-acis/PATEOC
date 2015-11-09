package main.sg.javapackage.ocd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.sg.javapackage.domain.Community;
import main.sg.javapackage.domain.CustomGraph;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;

/**
 * Implements the Speaker-listener Label Propagation Algorithm 
 * for overlapping community detection
 * Algorithm:SLPA(T, r )
 *	T : the user defined maximum iteration
 *	r: post-processing threshold
 *	1)	First, the memory of each node is initialized with a unique label.
 *	2)	Then, the following steps are repeated until the maximum iteration T is reached:
 *		a. 	One node is selected as a listener.
 *		b. 	Each neighbor of the selected node randomly selects a label with probability
 *			proportional to the occurrence frequency of this label in its memory and sends
 *			the selected label to the listener.
 *		c. The listener adds the most popular label received to its memory.
 *	3)	Finally, the post-processing based on the labels in the memories and the threshold
 *		r is applied to output the communities.
 *
 *
 */
public class SLPA {
	
	//This holds the input graph 
	CustomGraph workingGraph;
	
	//r: post-processing threshold
	double threshHold = 0.25f;
	
	//T : the user defined maximum iteration
	int iterations = 100;
	
	//constructor
	public SLPA() {

	}
	
	public void runSLPA(int timestep) {

		//retrieve internal graph
		workingGraph = PreProcessing.getParticularGraph(timestep);
		// Step 2 of the SLAP algorithm where memory labels are updated on each iteration
		propogateMemorylabel();
		// Step 3:post-processing based on the labels in the memories and the
		postProcessing(timestep);

	}
	
	/**
	 * The following steps are repeated until the maximum iteration T is reached:
	 *	a. 	One node is selected as a listener.
	 *	b. 	Each neighbor of the selected node randomly selects a label with probability
	 *		proportional to the occurrence frequency of this label in its memory and sends
	 *		the selected label to the listener.
	 *	c. The listener adds the most popular label received to its memory.
	 */
	private void propogateMemorylabel() {
		
		Set<Node> nodes = workingGraph.vertexSet();
		List<Node> nodeslist = new ArrayList<Node>();
		nodeslist.addAll(nodes);
		for (Node node: nodeslist)
			node.initializeSLPAVaribles();		
		
		nodeslist.hashCode();
		//Loop iteration T number of times
		for (int i = 1; i <=iterations; i++) {
			Collections.shuffle(nodeslist);
			for(Node node : nodeslist){
				node.listen(workingGraph);
			}
		}
	}
	
	
	/**
	 * This function implements the post-processing based on the labels in the
	 * memories and the threshold r is applied to output the communities
	 */
	private void postProcessing(int timestep) {

		Map<Integer, Set<Node>> slpaCommunitySet = new HashMap<Integer, Set<Node>>();
		
		for(Node node : workingGraph.vertexSet()){
			//Get the memory map of the node with label as key
			//and count as value
			Map<Integer, Integer> tmpMemoryMap = node.getMemoryMap();
			//get the number of communities this node belongs to
			int noOfCommunities = node.getNoOfCommunities();
			//Iterate through the memory map
			for(Map.Entry<Integer, Integer> entry : tmpMemoryMap.entrySet()){
				Integer labelId = entry.getKey();
				Integer count = entry.getValue();
				// Calculate the ratio of the label count against total number
				// of communities 
				double probalityDensity = (double)count/noOfCommunities;
				//If ration is greater than threhsold input, then add this node to the community
				//identified by the label.
				if(probalityDensity >= threshHold) {
					//Check if the label exits in the community map and if it doesnt
					//exist create one for this label and add the node to the set.
					if(slpaCommunitySet.get(labelId) == null) { 
						Set<Node> communityNodes = new HashSet<Node>();
						slpaCommunitySet.put(labelId,communityNodes);
						communityNodes.add(node);
					} else {
						Set<Node> communityNodes = slpaCommunitySet.get(labelId);
						communityNodes.add(node);
					}	
				}
			}
			
		}
		
		List<Integer> timestepCommunities = new ArrayList<Integer>();
		for(int i=0; i<=workingGraph.vertexSet().size(); i++ ){
			if(slpaCommunitySet.get(i) != null){
				timestepCommunities.add(i);
			}
		}
		
		Community[] tempComm = new Community[timestepCommunities.size()+1];
		for(int i=1; i<=timestepCommunities.size(); i++ ){
			tempComm[i] = new Community();
			tempComm[i].setId((long)i);
		}
		
		//Set the 0th Node as header node with long id as number of communities
		tempComm[0] = new Community();
		tempComm[0].setId((long) timestepCommunities.size()); 
		tempComm[0].setHeaderLabel();
		
		int communityCount=0;
		for(Integer label: timestepCommunities){
			communityCount++;
			List<Node> nodes = new ArrayList<Node>();
			nodes.addAll(slpaCommunitySet.get(label));
			tempComm[communityCount].setNodeList(nodes);
			
		}
		OverlapCommunityDetection.Communities.put(timestep, tempComm);
		
		//printer statements
		Logger.writeToLogln("Graph "+timestep);
		for(int i=1; i<=communityCount; i++ ){
			Logger.writeToLogln("	Community "+ i + " has "+ tempComm[i].getNodeList().size() + " nodes");
		}
		Logger.writeToLogln("Number of communities :" + communityCount);
		System.out.println("Graph "+timestep+ " has "+ communityCount+" communities");
	}
}
