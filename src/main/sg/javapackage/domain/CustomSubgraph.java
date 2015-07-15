package main.sg.javapackage.domain;

import java.util.Set;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.UndirectedWeightedSubgraph;

public class CustomSubgraph extends UndirectedWeightedSubgraph<Node, DefaultWeightedEdge>{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 388200494911710898L;

	
	/**
     * Creates a new undirected weighted subgraph.
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     * @param vertexSubset vertices to include in the subgraph. If <code>
     * null</code> then all vertices are included.
     * @param edgeSubset edges to in include in the subgraph. If <code>
     * null</code> then all the edges whose vertices found in the graph are
     * included.
     */
	public CustomSubgraph(WeightedGraph<Node, DefaultWeightedEdge> base,
	        Set<Node> vertexSubset,
	        Set<DefaultWeightedEdge> edgeSubset){
		
		super( base, vertexSubset, edgeSubset);
	}

}
