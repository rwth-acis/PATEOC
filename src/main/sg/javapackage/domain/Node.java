package main.sg.javapackage.domain;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.jgrapht.Graphs;

/**
 * Custom data structure to hold each node (vertex) of the undirected graph
 * @author Stephen
 *
 */
public class Node {
	
	/**
	 * Node attributes
	 */
	public static final String DEFAULT_NODE_LABEL = "Default Node";
	private Long id;
	private String label = DEFAULT_NODE_LABEL;
	private double attr_DegreeCentrality = Double.NaN;
	private double attr_ClosenessCentrality = Double.NaN;
	private double attr_EigenCentrality = Double.NaN;
	private boolean attr_isLeader = false;
	
	/**
	 * Constructors
	 * @param n
	 */
	public Node(Node n){
		this.id = n.id;
		this.label = n.getLabel();
		
	}
	public Node(long id, String label){
		this.id = id;
		this.label = label;
		initializeSLPAVaribles(); /*initialize SLPA paramters for each node */
	}
	
	
	/**
	 * GETTERS/SETTERS
	 * 
	 */
	public void setId(long id){
		this.id = id;
	}
	public Long getId(){
		return this.id;
	}
	public String getLabel(){
		return this.label;
	}
	public void setLabel(String label){
		this.label = label;
	}
	
	public void setDegreeCentrality(double value){
		this.attr_DegreeCentrality= value;
	}
	public double getDegreeCentrality(){
		return this.attr_DegreeCentrality;
	}
	
	public void setClosenessCentrality(double value){
		this.attr_ClosenessCentrality= value;
	}
	public double getClosenessCentrality(){
		return this.attr_ClosenessCentrality;
	}
	
	public void setEigenCentrality(double value){
		this.attr_EigenCentrality= value;
	}
	public double getEigenCentrality(){
		return this.attr_EigenCentrality;
	}
	
	public void setAsLeader(){
		this.attr_isLeader= true;
	}
	public boolean getIsLeader(){
		return this.attr_isLeader;
	}

	///////////////// SLPA SUPPORT METHODS /////////////////////
	//////////////////////////////////////////////////////
	/**
	 * SLPA ALGORITHM SUPPORT FUNCTIONS
	 */
	
//	//Adjacenecy list holding all the neighbours of the given node
//	private Set<Node> neighbhours;
//		
//	// Memory map used to hold the labelId and the count used for SLPA
//	// algorithm.
	private Map<Integer, Integer> memoryMap;
//	
//	// This represents the total number of counts(or communities) present in the
//	// memory map of this node.
	private int noOfCommunities;
//
//	/**
//	 * Constructor to create the node structure.
//	 */
//	public Node(Integer source) {
//		nodeId = source;
//		initializeDataStructure();
//	}

//	/**
//	 * Helper method to initialize the required data strucutres. This also makes
//	 * sure that the memory of each node is initialized with a unique label as
//	 * part of SLPA algorithm.
//	 */
	public void initializeSLPAVaribles() {
		memoryMap = new LinkedHashMap <Integer, Integer>();
		noOfCommunities = 1;
		memoryMap.put((int) (long) this.id, 1);
	}
//	
//	
	/**
	 * This function implements the listen step of the SLPA algorithm. Each
	 * neighbor sends the selected label to the listener and the listener adds
	 * the most popular label received to its memory.
	 */
	public void listen(CustomGraph workingGraph) {
		//Map to hold the all the received labels from its neighbours in this iteration
		Map<Integer, Integer> labelMap = new HashMap<Integer, Integer>();
		
		List<Node> neighbhours = Graphs.neighborListOf(workingGraph, this);

		//Iterate through all the neighbors and callk speak on them as part of SLPA algorithm.
		for (Node neighbour : neighbhours) {
			//Speak method returns label to the listener
			int labelNo = neighbour.speak();
			//Add the label received to the temporary labelMap to hold the labelId and received count.
			if (labelMap.get(labelNo) == null) {
				labelMap.put(labelNo, 1);
			} else {
				int currentLabelCount = labelMap.get(labelNo);
				currentLabelCount++;
				labelMap.put(labelNo, currentLabelCount);
			}
		}
		//After all neighbours sends the label, findout the most popular label
		int popularLabel = getMostPopularLabel(labelMap);
		
		//add the popular label to the memory map of the node.
		if(memoryMap.get(popularLabel) == null) {
			memoryMap.put(popularLabel, 1);
		} else {
			int currentCount = memoryMap.get(popularLabel);
			currentCount++;
			memoryMap.put(popularLabel, currentCount);
		}
		//Increment the noOfCommunities
		noOfCommunities++;
		labelMap.clear();
	}

	private int getMostPopularLabel(Map<Integer, Integer> labelMap) {
		int maxLabelCount = 0;
		int popularLabel= -1;
		for (Map.Entry<Integer, Integer> entry : labelMap.entrySet()) {
			Integer labelId = entry.getKey();
			Integer labelCount = entry.getValue();
			if (labelCount > maxLabelCount) {
				popularLabel = labelId;
				maxLabelCount = labelCount;
			}
		}
		return popularLabel;
	}

//	/**
//	 * Each neighbor of the selected node randomly selects a label with probability
//	 * proportional to the occurrence frequency of this label in its memory and sends
//	 * the selected label to the listener.
//	 * @return label
//	 */
	private int speak() {
		//generate a random double value
		Random random = new Random();
		double randomDoubleValue = random.nextDouble();
		double cumulativeSum = 0;
		// Randomly select a label with probability proportional to the
		// occurrence frequency of this label in its memory
		for (Map.Entry<Integer, Integer> entry : memoryMap.entrySet()) {
			Integer labelId = entry.getKey();
			Integer labelCount = entry.getValue();
			cumulativeSum = cumulativeSum + ((double)labelCount)/noOfCommunities;
			if(cumulativeSum >= randomDoubleValue) {
				return labelId;
			}
		}
		return (int) (long) this.id;
	}
//	
//	//Getters and Setters
//	/**
//	 * Adds a neighbor to the node's adjacency list
//	 * @param destNode
//	 */
//	public void addNeighbour(Node destNode) {
//		neighbhours.add(destNode);
//	}
//
//	/**
//	 * Returns the neighbors of the node
//	 * @return set of neighbors
//	 */
//	public Set<Node> getNeighbhours() {
//		return neighbhours;
//	}
//
//	/**
//	 * Returns the NodeId
//	 * @return node ID
//	 */
//	public int getNodeId() {
//		return nodeId;
//	}
//
//	/**
//	 * Returns the memory map of the node at given time t
//	 * @return map
//	 */
	public Map<Integer, Integer> getMemoryMap() {
		return memoryMap;
	}
//
//	/**
//	 * Returns the total number of entries in the memory map's count
//	 * @return int
//	 */
	public int getNoOfCommunities() {
		return noOfCommunities;
	}
}
