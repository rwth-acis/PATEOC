package main.sg.javapackage.domain;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class CustomGraph extends SimpleWeightedGraph<Node, DefaultWeightedEdge>{

	//CustomGraph g = new CustomGraph(DefaultWeightedEdge.class);

	
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
