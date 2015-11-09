package main.sg.javapackage.domain;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
/**
 * Custom data structure to hold undirected graphs on which all operations are performed
 * @author Stephen
 *
 */
public class CustomGraph extends SimpleWeightedGraph<Node, DefaultWeightedEdge>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3184992753356510439L;
	
	/**
     * Creates a new simple weighted graph with the specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     */
    public CustomGraph(EdgeFactory<Node, DefaultWeightedEdge> ef)
    {
        super(ef);
    }

    /**
     * Creates a new simple weighted graph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public CustomGraph(Class<? extends DefaultWeightedEdge> edgeClass)
    {
        this(new ClassBasedEdgeFactory<Node, DefaultWeightedEdge>(edgeClass));
    }

}
