package main.sg.javapackage.ext.graphml;

import java.io.FileWriter;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;

import main.sg.javapackage.domain.CustomGraph;
import main.sg.javapackage.domain.CustomSubgraph;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.logging.Logger;

import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.xml.sax.SAXException;

/**
 * Interface implementation to export time series graph into GRAPHML file
 * @author Stephen
 *
 */
public class CustomGraphMLExporter {
	/**
	 * export graph without any additional details
	 * @param Graph input graph
	 * @param graphID time-frame of the graph
	 */
	public static void GraphMLExport(CustomGraph Graph, int graphID) {

		Logger.writeToLogln("Exported Graph "+graphID+" to GraphML file.");
		GraphMLExporter<Node, DefaultWeightedEdge> VE = new GraphMLExporter<Node, DefaultWeightedEdge>(); 
		
		try {
			FileWriter PS  = new FileWriter("bin\\TimeGraph_"+graphID+".graphml");
			VE.export(PS, Graph); 
			PS.close();
		} catch (IOException | TransformerConfigurationException | SAXException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * export graph with additional vertex and edge details 
	 * @param Graph
	 * @param graphID
	 */
	public static void GraphMLExportWithProperties(final CustomGraph Graph, int graphID) {

		Logger.writeToLogln("Exported Graph "+graphID+" to GraphML file.");
		
		VertexNameProvider<Node> vertexIDProvider = new VertexNameProvider<Node>() { 
			@Override 
			public String getVertexName(Node vertex) { 
				return vertex.getId().toString(); 
			} 
		};
		VertexNameProvider<Node> vertexLabelProvider = new VertexNameProvider<Node>() { 

             @Override 
             public String getVertexName(Node vertex) { 
                 return vertex.getLabel(); 
             } 
		}; 

		EdgeNameProvider<DefaultWeightedEdge> edgeIDProvider = new EdgeNameProvider<DefaultWeightedEdge>() { 
             @Override 
             public String getEdgeName(DefaultWeightedEdge edge) { 
                 return Graph.getEdgeSource(edge).getLabel() + " - " + Graph.getEdgeTarget(edge).getLabel();
             } 
		}; 
		
		EdgeNameProvider<DefaultWeightedEdge> edgeLabelProvider = new EdgeNameProvider<DefaultWeightedEdge>() { 
	         @Override 
	         public String getEdgeName(DefaultWeightedEdge edge) { 
	             return Graph.getEdgeWeight(edge)+""; 
	         } 
		}; 
		
		GraphMLExporter<Node, DefaultWeightedEdge> VE = new GraphMLExporter<Node, DefaultWeightedEdge>(vertexIDProvider,vertexLabelProvider,edgeIDProvider,edgeLabelProvider); 
		try {
			FileWriter PS  = new FileWriter("bin\\TimeGraph_"+graphID+".graphml");
			VE.export(PS, Graph); 
			PS.close();
		} catch (IOException | TransformerConfigurationException | SAXException e) {
			e.printStackTrace();
		} 
	}
	
	public static void GraphMLExportSubgraph(final CustomSubgraph Subgraph, int subgraphID) {

		Logger.writeToLogln("Exported Graph "+subgraphID+" to GraphML file.");
		
		VertexNameProvider<Node> vertexIDProvider = new VertexNameProvider<Node>() { 
			@Override 
			public String getVertexName(Node vertex) { 
				return vertex.getId().toString(); 
			} 
		};
		VertexNameProvider<Node> vertexLabelProvider = new VertexNameProvider<Node>() { 

             @Override 
             public String getVertexName(Node vertex) { 
                 return vertex.getLabel(); 
             } 
		}; 

		EdgeNameProvider<DefaultWeightedEdge> edgeIDProvider = new EdgeNameProvider<DefaultWeightedEdge>() { 
             @Override 
             public String getEdgeName(DefaultWeightedEdge edge) { 
                 return Subgraph.getEdgeSource(edge).getLabel() + " - " + Subgraph.getEdgeTarget(edge).getLabel(); 
             } 
		}; 
		
		EdgeNameProvider<DefaultWeightedEdge> edgeLabelProvider = new EdgeNameProvider<DefaultWeightedEdge>() { 
	         @Override 
	         public String getEdgeName(DefaultWeightedEdge edge) { 
	             return Subgraph.getEdgeWeight(edge)+""; 
	         } 
		}; 
		
		GraphMLExporter<Node, DefaultWeightedEdge> VE = new GraphMLExporter<Node, DefaultWeightedEdge>(vertexIDProvider,vertexLabelProvider,edgeIDProvider,edgeLabelProvider); 
		//GraphMLExporter<Node, DefaultWeightedEdge> VE = new GraphMLExporter<Node, DefaultWeightedEdge>(); 
		
		try {
			FileWriter PS  = new FileWriter("bin\\TimeGraph_"+subgraphID+".graphml");
			VE.export(PS, Subgraph); 
			PS.close();
		} catch (IOException | TransformerConfigurationException | SAXException e) {
			e.printStackTrace();
		} 
	}

}
