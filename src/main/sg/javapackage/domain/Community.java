package main.sg.javapackage.domain;

import java.util.ArrayList;
import java.util.List;

import main.sg.javapackage.domain.GlobalVariables.Evolution;


public class Community {
	
	public static final String DEFAULT_COMMUNITY_LABEL = "Community_Default";
	public static final String DEFAULT_COMMUNITY_HEADER = "Community_Header";
	private String comLabel;
	private Long id;
	private List<Node> listOfNodes;
	private int num_Nodes = 0;
	private int num_Edges = 0;
	private int num_Leaders = 0;
	private double attr_SizeRatio = 0.0;
	private double attr_LeadersRatio = 0.0;
	private double attr_Density = 0.0;
	private double attr_Cohesion = 0.0;
	private double attr_ClusteringCoefficient = 0.0;
	private double attr_DegreeCentrality = 0.0;
	private double attr_ClosenessCentrality = 0.0;
	private double attr_EigenVectorCentrality = 0.0;
	private double attr_Assortativity = 0.0;
	private double attr_LDegreeCentrality = 0.0;
	private double attr_LClosenessCentrality = 0.0;
	private double attr_LEigenVectorCentrality = 0.0;
	private double attr_LAssortativity = 0.0;
	private Evolution previousEvent =null;
	private Evolution nextEvent =null;
	private Community previousCommunityInTime = null;
	
	public Community(){
		this.comLabel = DEFAULT_COMMUNITY_LABEL;
		listOfNodes = new ArrayList<Node>();
	}
	
	///////////////// GETTER/SETTERS /////////////////////
	//////////////////////////////////////////////////////
	
	public void setHeaderLabel(){
		//If label = header, id represents number of communities in that timestep
		this.comLabel=DEFAULT_COMMUNITY_HEADER;
	}
	public String getLabel(){
		return comLabel;
	}
	
	public void setId(Long id){
		this.id = id;
	}
	public Long getId(){
		return id;
	}

	public List<Node> getNodeList(){
		return this.listOfNodes;
	}
	public void setNodeList(List<Node> nodes){
		listOfNodes = new ArrayList<Node>(nodes); //copy the passed list into listOfNodes
	}
	public void addNode(Node node){
		this.listOfNodes.add(node);
	}
	public int nodelistSize(){
		return this.listOfNodes.size();
	}
	
	public double getAttrSizeRatio(){
		return this.attr_SizeRatio;
	}
	public void setAttrSizeRatio(double value){
		attr_SizeRatio = value;
	}
	
	public double getAttrDensity(){
		return this.attr_Density;
	}
	public void setAttrDensity(double value){
		attr_Density = value;
	}
	
	public double getAttrClusteringCoefficient(){
		return this.attr_ClusteringCoefficient;
	}
	public void setAttrClusteringCoefficient(double value){
		attr_ClusteringCoefficient = value;
	}
	
	public double getAttrCohesion(){
		return this.attr_Cohesion;
	}
	public void setAttrCohesion(double value){
		attr_Cohesion = value;
	}
	
	public double getAttrDegreeCentrality(){
		return this.attr_DegreeCentrality;
	}
	public void setAttrDegreeCentrality(double value){
		attr_DegreeCentrality = value;
	}
	
	public double getAttrClosenessCentrality(){
		return this.attr_ClosenessCentrality;
	}
	public void setAttrClosenessCentrality(double value){
		attr_ClosenessCentrality = value;
	}
	
	public double getAttrLeaderRatio(){
		return this.attr_LeadersRatio;
	}
	public void setAttrLeaderRatio(){
		this.attr_LeadersRatio = calculateAttrLeaderRatio();
	}
	
	private double calculateAttrLeaderRatio(){
		if(listOfNodes.size() != 0 && num_Leaders > 0){
			return ((double) num_Leaders / listOfNodes.size());
		}
		return 0.0;
	}
	
	public void setNumLeaders(int value){
		this.num_Leaders = value;
	}
	public int getNumLeaders(){
		return this.num_Leaders;
	}
	
	public void setNumNodes(int value){
		this.num_Nodes = value;
	}
	public int getNumNodes(){
		return this.num_Nodes;
	}
	
	public void setNumEdges(int value){
		this.num_Edges = value;
	}
	public int getNumEdges(){
		return this.num_Edges;
	}
	
	public void setPreviousCommunity(Community comm){
		this.previousCommunityInTime = comm;
	}
	public Community getPreviousCommunity(){
		return this.previousCommunityInTime;
	}
	public boolean existsPreviousCommunity(){
		
		if(this.previousCommunityInTime != null){
			return true;
		}
		return false;
	}
	
	public double getAttrAssortativity(){
		return this.attr_Assortativity;
	}
	public void setAttrAssortativity(double value){
		attr_Assortativity = value;
	}
	
	public double getAttrEigenVectorCentrality(){
		return this.attr_EigenVectorCentrality;
	}
	public void setAttrEigenVectorCentrality(double value){
		attr_EigenVectorCentrality = value;
	}
	
	public double getAttrLeaderEigenVectorCentrality(){
		return this.attr_LEigenVectorCentrality;
	}
	public void setAttrLeaderEigenVectorCentrality(double value){
		attr_LEigenVectorCentrality = value;
	}
	
	public double getAttrLeaderDegreeCentrality(){
		return this.attr_LDegreeCentrality;
	}
	public void setAttrLeaderDegreeCentrality(double value){
		attr_LDegreeCentrality = value;
	}
	
	public double getAttrLeaderClosenessCentrality(){
		return this.attr_LClosenessCentrality;
	}
	public void setAttrLeaderClosenessCentrality(double value){
		attr_LClosenessCentrality = value;
	}
	
	public double getAttrLeaderAssortativity(){
		return this.attr_LAssortativity;
	}
	public void setAttrLeaderAssoratativity(double value){
		attr_LAssortativity = value;
	}
	
	public void setEvolution(Evolution e){
		this.nextEvent = e;
	}
	public Evolution getEvolution(){
		return this.nextEvent;
	}
	
	public void setPreviousEvolution(Evolution e){
		this.previousEvent = e;
	}
	public Evolution getPreviousEvolution(){
		return this.previousEvent;
	}

}
