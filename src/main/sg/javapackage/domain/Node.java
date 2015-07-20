package main.sg.javapackage.domain;

public class Node {
	
	public static final String DEFAULT_NODE_LABEL = "Default Node";
	private Long id;
	private String label = DEFAULT_NODE_LABEL;
	private double attr_DegreeCentrality = Double.NaN;
	private double attr_ClosenessCentrality = Double.NaN;
	private double attr_BetweenessCentrality = Double.NaN;
	private double attr_EigenCentrality = Double.NaN;
	private double attr_AssortativityValue = Double.NaN;
	private boolean attr_isLeader = false;
	
	public Node(){
		this.id = (long) 0;
		//label = 0+"";
	}
	public Node(Node n){
		this.id = n.id;
		this.label = n.getLabel();
	}
	
	///////////////// GETTER/SETTERS /////////////////////
	//////////////////////////////////////////////////////
	
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
	
	public void setBetweenessCentrality(double value){
		this.attr_BetweenessCentrality= value;
	}
	public double getBetweenessCentrality(){
		return this.attr_BetweenessCentrality;
	}
	
	public void setEigenCentrality(double value){
		this.attr_EigenCentrality= value;
	}
	public double getEigenCentrality(){
		return this.attr_EigenCentrality;
	}
	
	public void setAssortativityValue(double value){
		this.attr_AssortativityValue = value;
	}
	public double getAssortativityValue(){
		return this.attr_AssortativityValue;
	}
	
	public void setAsLeader(){
		this.attr_isLeader= true;
	}
	public boolean getIsLeader(){
		return this.attr_isLeader;
	}

}
