package main.sg.javapackage.ext.graphml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import main.sg.javapackage.domain.CustomGraph;
import main.sg.javapackage.graph.PreProcessing;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CustomGraphMLImporter {
	
	//TODO:Not Tested and add weight edge when generating
		public static CustomGraph GraphMLImport(int timestep) throws SAXException, IOException, ParserConfigurationException{
			CustomGraph importedGraph = new CustomGraph(DefaultWeightedEdge.class);
			
			String inputPath;
			inputPath = "bin\\InGraph_" + timestep +".graphml"; 

			File fXmlFile = new File(inputPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			NodeList eList = doc.getElementsByTagName("edge");
			for (int iter = 0; iter < eList.getLength(); iter++) {
				org.w3c.dom.Node eNode = eList.item(iter);
				if (eNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					Element eElement = (Element) eNode;
					importedGraph.addVertex(PreProcessing.CompleteNodeList.get(eElement.getAttribute("source").toString()));
					importedGraph.addVertex(PreProcessing.CompleteNodeList.get(eElement.getAttribute("target").toString()));
					importedGraph.addEdge(PreProcessing.CompleteNodeList.get(eElement.getAttribute("source").toString()), 
							PreProcessing.CompleteNodeList.get(eElement.getAttribute("target").toString()));
				}
			}
			return importedGraph;
		}

}
