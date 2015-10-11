package main.sg.javapackage.metrics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.tc33.jheatchart.HeatChart;

public class HeatMap {
	
	private static double[][] matrix = new double[25][5];
	
	public HeatMap() {
		// TODO Auto-generated constructor stub
	}
	
	public static void initializeHeatMap(){
		//Matrix of 25 attributes and 5 events
		matrix = new double[25][5];
	}
	
	public static void aggregateHeatMap(Map<String,Integer> metricCounter ,int event){
		
		int i = 0;
		for(Map.Entry<String, Integer> entry : metricCounter.entrySet()){
			matrix[i][event-1] = entry.getValue();
			i++;
		}
		
	}
	
	public static void createHeatMap(){
		

        // Create our heat chart using our data.
        HeatChart chart = new HeatChart(matrix);
        // Customise the chart.

        // results for no and single changes to specs
        chart.setTitle("Selective Attributes HeatMap");
        chart.setTitleFont(new Font("SansSerif", Font.PLAIN, 30));
        chart.setXAxisLabel("Events");
        chart.setYAxisLabel("Features");
        
        String[] xValues= {"Survive", "Merge" , "Split" , "Dissolve", "Multiclass"};
        chart.setXValues(xValues);      
        
        //Preserving the input sequence of attributes
        String[] yValues= {"Cohesion","ClusteringCoeffecient","LEigenVectorCentrality","D_ClusteringCoeffecient",
        		"Density","EigenVectorCentrality","LClosenessCentrality","D_Density","PreviouSurvive",
        		"PreviousSplit","SpearmanRho","PreviousMerge","D_SpearmanRho","D_ClosenessCentrality",
        		"D_DegreeCentrality","D_EigenVectorCentrality","ClosenessCentrality","D_SizeRatio",
        		"PreviousDissolve","SizeRatio","D_LeaderRatio","DegreeCentrality","LeaderRatio",
        		"LDegreeCentrality","D_Cohesion"};
        chart.setYValues(yValues);
        
        chart.setHighValueColour(Color.BLUE);
        chart.setLowValueColour(Color.LIGHT_GRAY);
        chart.setAxisLabelsFont(new Font("SansSerif", Font.PLAIN, 25));
        chart.setCellSize(new Dimension(50,25));
        chart.setAxisValuesFont(new Font("SansSerif", Font.PLAIN, 18));
        
        // Output the chart to a file.
        try {
			chart.saveToFile(new File("bin\\Result_Heatmap.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
