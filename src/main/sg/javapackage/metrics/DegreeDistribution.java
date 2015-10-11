package main.sg.javapackage.metrics;

import java.util.HashMap;
import java.util.Map;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import main.sg.javapackage.domain.CustomGraph;
import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.visualize.ChartVisualization;

/**
 * Generates degree distribution bins for the 
 * input graph
 * Acts as metric to check power law compatability
 * 
 * @author Stephen
 *
 */
public class DegreeDistribution {
	
	private Map<Integer, Double> degreeDistribution = new HashMap<Integer, Double>();
	private Long nodeCount;
	private Long degreeCount;
	private static String resultFile = GlobalVariables.resultFile;
	
	public DegreeDistribution() {
		// TODO Auto-generated constructor stub
		nodeCount = 0L;
		degreeCount = 0L;
	}
	
	public void updateDegreeFrequency(CustomGraph graph){
		
		nodeCount = (long) graph.vertexSet().size();
		for (Node node : graph.vertexSet()){
			
			int nodeDegree = graph.degreeOf(node);
			if(!degreeDistribution.containsKey(nodeDegree)){
				degreeDistribution.put(nodeDegree,1.0);
			}
			else{
				double val = degreeDistribution.get(nodeDegree);
				val+=1.0d;
				degreeDistribution.put(nodeDegree,val);
			}
			degreeCount+=1;
		}
	}
	
	public void computeDegreeDistribution(){
		
		Logger.writeToFile(resultFile,"Total Node Count : "+nodeCount+"\n",true);
		Logger.writeToFile(resultFile,"Degree Distribution of the graph\nDegree : Frequency\n",true);
		for (Map.Entry<Integer, Double> entry : degreeDistribution.entrySet()){
			
			entry.setValue(entry.getValue()/(double)degreeCount);
			Logger.writeToFile(resultFile,entry.getKey() + " : "+entry.getValue()+"\n",true);    
        }
		
		//save plot as .png
		ChartVisualization.generateChart(convertResultToPlotDataset(), 0);
		clearContents();
		
	}
	
	private void clearContents(){
		nodeCount = 0L;
		degreeDistribution.clear();
	}
	
	private XYDataset convertResultToPlotDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("Binned Degree");
		for (Map.Entry<Integer, Double> entry : degreeDistribution.entrySet()){
			
			series.add(entry.getKey(),entry.getValue());
		}
		dataset.addSeries(series);
		return dataset;
	}
}

