package main.sg.javapackage.visualize;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ShapeUtilities;

/**
 * Primary module for rendering and saving the 
 * 2D plots in a .png file
 * @author Stephen
 *
 */
public class ChartVisualization extends JFrame{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ChartVisualization(XYDataset plotData,int event) {
		// TODO Auto-generated constructor stub
		super("PATEOC LineChart Renderer");
		
		JPanel chartPanel = createChartPanel(plotData,event);
		add(chartPanel, BorderLayout.CENTER);
		setSize(800, 640);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);	
		
	}
	
	private JFreeChart createClassificationAccuracyChart(XYDataset plotdata,String title){
		
		JFreeChart chart = null;
		String chartTitle = "Plot for Classification Accuracy vs Problem Class \n"+title;
		String xAxisLabel = "Problem Class";
		String yAxisLabel = "Algorithm Accuracy";
		
		chart = ChartFactory.createXYLineChart(chartTitle, 
				xAxisLabel, yAxisLabel, plotdata, PlotOrientation.VERTICAL, true, true, false );
		setLineChartOptions(chart);
		
		return chart;
	}
	
	private JFreeChart createDegreeDistributionChart(XYDataset plotdata){
		
		JFreeChart chart = null;
		String chartTitle = "Scatter Plot showing Degree Distribution \n following Power Law";
		String xAxisLabel = "Degree";
		String yAxisLabel = "Frequency";
		
		chart = ChartFactory.createScatterPlot(chartTitle, 
				xAxisLabel, yAxisLabel, plotdata, PlotOrientation.VERTICAL, true, true, false );
		setScatterChartOptions(chart);
		return chart;
	}
	
	private JPanel createChartPanel(XYDataset plotdata, int event){
				
		JFreeChart chart = null;
		File imageFile=null;
		String charttitle=null;
		if(event == 1){
			charttitle = "Survive Event";
			chart=createClassificationAccuracyChart(plotdata,charttitle);
			imageFile = new File("bin\\Result_Survive.png");
		}
		else if(event == 2){
			charttitle = "Merge Event";
			chart=createClassificationAccuracyChart(plotdata,charttitle);
			imageFile = new File("bin\\Result_Merge.png");
		}
		else if(event == 3){
			charttitle = "Split Event";
			chart=createClassificationAccuracyChart(plotdata,charttitle);
			imageFile = new File("bin\\Result_Split.png");
		}	
		else if(event == 4){
			charttitle = "Dissolve Event";
			chart=createClassificationAccuracyChart(plotdata,charttitle);
			imageFile = new File("bin\\Result_Dissolve.png");
		}
		else if(event == 5){
			chart=createDegreeDistributionChart(plotdata);
			imageFile = new File("bin\\Result_DegreeDistribution.png");
		}
		else{
			System.out.println("Invalid event passed for chart generation");
			return new ChartPanel(chart);
		}

		// saves the chart as an image files
		try {
			int width = 800;
			int height = 640;
			ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
		} catch (IOException ex) {
			System.err.println(ex);
		}

		return new ChartPanel(chart);
	}
	
	private void setLineChartOptions(JFreeChart chart) {
		XYPlot plot = chart.getXYPlot();
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(40.0, 100.0);
		
		ValueAxis domainAxis = plot.getDomainAxis();
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		XYSplineRenderer renderer = new XYSplineRenderer();
		
		// sets thickness for series (using strokes)
		renderer.setBaseStroke(new BasicStroke(3.0f));
		renderer.setAutoPopulateSeriesStroke(false);

		// sets the points' label
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        XYItemLabelGenerator generator =
            new StandardXYItemLabelGenerator(
                StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT,
                format, format);
        renderer.setBaseItemLabelGenerator(generator);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("SansSerif", Font.BOLD, 13));
        
		// sets paint color for plot outlines
		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));
		
		// sets renderer for lines
		plot.setRenderer(renderer);
		
		// sets plot background
		plot.setBackgroundPaint(Color.white);
		
		// sets paint color for the grid lines
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		
	}
	
	private void setScatterChartOptions(JFreeChart chart){
		
		XYPlot plot = chart.getXYPlot();
		XYItemRenderer renderer = plot.getRenderer();
		Shape cross = ShapeUtilities.createDiagonalCross(4, 0.8f);
		renderer.setSeriesShape(0, cross);
		
		// sets paint color for plot outlines
		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));
		
		// sets renderer for lines
		plot.setRenderer(renderer);
		
		// sets plot background
		plot.setBackgroundPaint(Color.white);
		
		// sets paint color for the grid lines
		plot.setRangeGridlinesVisible(false);
		plot.setRangeGridlinePaint(Color.BLACK);
		
		plot.setDomainGridlinesVisible(false);
		plot.setDomainGridlinePaint(Color.BLACK);
	}
	
	public static void generateChart(XYDataset dataset,int event){
		
        final int e = event;
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	new ChartVisualization(dataset, e).setVisible(false);
            }
        });
	}
	
}
