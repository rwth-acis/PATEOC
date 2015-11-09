package main.sg.javapackage.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import main.sg.javapackage.domain.CustomGraph;
import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.domain.GlobalVariables.Algorithm;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.ext.graphml.CustomGraphMLExporter;
import main.sg.javapackage.logging.Logger;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Pre-Processing Module responsible for reading input graph and formulating
 * customgraph data structure for each snapshot of the time series graph
 * @author Stephen
 *
 */
public class PreProcessing {

	//path to directory holding the input graph files
	private static String basePath;	
	
	//total input graphs 
	private static int totalGraphs;
	
	//selected OCD algorithm
	private static Algorithm algo;
	
	//Primary data structure to hold every new node (to later build a recurring network)
	public static Map<String,Node> CompleteNodeList = new HashMap<String,Node>();
	
	//default edge weight
	static final double DEFAULT_EDGE_WEIGHT=1.0;
	
	/**
	 * Constructor
	 * @param BasePath
	 * @param totalTimesteps
	 * @param selectedAlgo
	 */
	public PreProcessing(String BasePath, int totalTimesteps, Algorithm selectedAlgo){
		PreProcessing.basePath = BasePath;
		PreProcessing.totalGraphs = totalTimesteps;
		PreProcessing.algo = selectedAlgo;	
	}
	
	
	/**
	 * Store the input graphs as nodelist 
	 * in MAP structure for preprocessing purpose
	 * 
	 * @param input graph filepath
	 * 			from constructor
	 * 
	 * @param total number of graphs
	 * 			from constructor
	 * 
	 * @throws IOException
	 */
	public void preprocessInputGraph() {
		
		int timestep=1;
		long counter = 0;
		
		//determine the delimiter to use
		String delimiter = evalauteFileDelimiter();
		
		Logger.writeToLogln("Total number of input graphs: "+totalGraphs);
		Logger.writeToLogln("Selected algorithm on the graphs: "+algo.toString().toUpperCase());

		//Process each file corresponding to each timestep
		while(timestep <= totalGraphs) { 

			//read input file
			File inputFile= new File(formulateInputPath(timestep));
			try (BufferedReader reader 
					= new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)))) {
				String fileline = null; 
				
				//read each line
				while( (fileline = reader.readLine())!= null ){
					String [] vertices = fileline.split(delimiter); 
					
					//skip any header/comment
					if(vertices[0].startsWith(";") || vertices[0].startsWith("#")){
						continue;
					}
					
					//create a custom Node structure for each vertex
					Node n1, n2;
					if(!CompleteNodeList.containsKey((vertices[0].toString()))) {
						n1 = new Node(++counter,vertices[0]);
						CompleteNodeList.put(vertices[0], n1);
					}
					if(!CompleteNodeList.containsKey((vertices[1].toString()))) {
						n2 = new Node(++counter,vertices[1]);
						CompleteNodeList.put(vertices[1], n2);
					}
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(e.getMessage()+"\nReturn Code :111");
			}
			
			timestep++;
		}
		Logger.writeToLogln("Total number of unique nodes processed from all graphs : "+CompleteNodeList.size());
		
	}
	
	/**
	 * Constructs a JgraphT graph 
	 * from input edgelist 
	 * 
	 * @param timestep corresponding 
	 * 			to graph in timeframe
	 * 
	 * @return graph
	 */
	public static CustomGraph loadInputGraphEdgeList(int timestep){
		
		//Undirected graph
		CustomGraph inputGraph = new CustomGraph(DefaultWeightedEdge.class);
		
		//determine delimiter
		String delimiter = evalauteFileDelimiter();

		//read input file
		File inputFile= new File(formulateInputPath(timestep));
		try (BufferedReader reader 
				= new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)))) {
			String fileline = null; 
			
			//read each line
			while( (fileline = reader.readLine())!= null ){

				String [] vertices = fileline.split(delimiter); //\\s+ 
				
				//skip header/comment
				if(vertices[0].startsWith(";") || vertices[0].startsWith("#")){
					continue;
				}
				
				//build an undirected graph from input file
				//If vertex already exists in graph, no action performed
				inputGraph.addVertex(CompleteNodeList.get((vertices[0].toString())));
				inputGraph.addVertex(CompleteNodeList.get((vertices[1].toString())));
				DefaultWeightedEdge edge = null;
				
				//No Self loops
				if(!vertices[0].equalsIgnoreCase(vertices[1])){
					edge = inputGraph.addEdge(CompleteNodeList.get((vertices[0].toString())), 
							CompleteNodeList.get((vertices[1].toString())));
				}
				
				//check 3rd column for edge weight
				if(edge!=null){
					if(vertices.length >2 )
						inputGraph.setEdgeWeight(edge, Double.parseDouble(vertices[2]));
					else
						inputGraph.setEdgeWeight(edge, DEFAULT_EDGE_WEIGHT);
				}
				else{
					//edge already exists
					//do nothing
				}

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage()+"\nReturn Code :111");
		}
		if(GlobalVariables.graphExtract){
			CustomGraphMLExporter.GraphMLExportWithProperties(inputGraph,timestep);
		}

		return inputGraph;
	}
	
	/**
	 * determines the file delimiter based
	 * on the selected algorithm
	 * 
	 * @param selected algorithm from constructor
	 * @return String
	 */
	private static String evalauteFileDelimiter(){
		
		// \\t+ means any number of tabspace between tokens, \\s+ for space
		if(PreProcessing.algo == Algorithm.focs){
			return "\\s+";
		}
		return "\\t+";
	}
	
	/**
	 * formulates the path to input file 
	 * for each timestep file by suffixing
	 * timestep to the basepath - "file_i"
	 *  
	 * @param basepath from constructor
	 * @param timestep
	 * 
	 * @return String
	 */
	private static String formulateInputPath(int timestep){
		String inputPath;
		inputPath = basePath.substring(0,basePath.length()-5);
		return (System.getProperty("user.dir").concat("\\"+inputPath + timestep + ".txt"));
	}
	
	/**
	 * Returns number of graphs processed;
	 * Corresponding to number of input files
	 * 
	 * @return integer
	 */
	public static int totalGraphCount() {
		return totalGraphs;
	}
	
	/**
	 * Returns total number of nodes in
	 * the MAP of master list 
	 * 
	 * @return integer
	 */
	public static int totalNodeCount(){
		return CompleteNodeList.size();
	}
	
	/**
	 * Returns the node from MAP 
	 * corresponding to the nodevalue
	 * 
	 * @param nodeValue node's label
	 * 
	 * @return Node
	 */
	public static Node masterlistGetNode(String nodeValue){
		return CompleteNodeList.get(nodeValue);
	}
	
	/**
	 * Returns the graph of type JgraphT
	 * from the inpur Edgelist
	 * 
	 * @param timestep
	 * 
	 * @return CustomGraph
	 */
	public static CustomGraph getParticularGraph(int timestep){
		return loadInputGraphEdgeList(timestep);
	}

}
