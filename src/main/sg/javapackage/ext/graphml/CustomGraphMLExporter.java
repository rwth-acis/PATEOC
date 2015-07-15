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


public class CustomGraphMLExporter {
	
	public static void GraphMLExport(CustomGraph Graph, int graphID) {

		Logger.writeToLogln("Exported Graph "+graphID+" to GraphML file.");
		GraphMLExporter<Node, DefaultWeightedEdge> VE = new GraphMLExporter<Node, DefaultWeightedEdge>(); 
		
		try {
			FileWriter PS  = new FileWriter("bin\\TimeGraph_"+graphID+".graphml");
			VE.export(PS, Graph); 
			PS.close();
		} catch (IOException | TransformerConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void GraphMLExportWithProperties(CustomGraph Graph, int graphID) {

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	@SuppressWarnings("unused")
	public static void GraphMLExportSubgraph(CustomSubgraph Subgraph, int subgraphID) {

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
		
		//GraphMLExporter<Node, DefaultWeightedEdge> VE = new GraphMLExporter<Node, DefaultWeightedEdge>(vertexIDProvider,vertexLabelProvider,edgeIDProvider,edgeLabelProvider); 
		GraphMLExporter<Node, DefaultWeightedEdge> VE = new GraphMLExporter<Node, DefaultWeightedEdge>(); 
		
		try {
			FileWriter PS  = new FileWriter("bin\\TimeGraph_"+subgraphID+".graphml");
			VE.export(PS, Subgraph); 
			PS.close();
		} catch (IOException | TransformerConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
