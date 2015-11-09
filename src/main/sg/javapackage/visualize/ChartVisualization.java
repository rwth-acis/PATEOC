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
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
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
	
	/**
	 * constructor and initializer of render panel
	 * @param plotData
	 * @param event
	 */
	public ChartVisualization(XYDataset plotData,int event) {

		super("PATEOC LineChart Renderer");
		
		JPanel chartPanel = createChartPanel(plotData,event);
		add(chartPanel, BorderLayout.CENTER);
		
		//dimensions
		setSize(800, 640);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);	
		
	}
	
	/**
	 * creates the JFreeChart panel to render the accuracy chart
	 * @param plotdata
	 * @param title
	 * @return
	 */
	private JFreeChart createClassificationAccuracyChart(XYDataset plotdata,String title){
		
		JFreeChart chart = null;
		String chartTitle = "Classification Accuracy vs Problem Class \n"+title;
		String xAxisLabel = "Problem Class";
		String yAxisLabel = "Algorithm Accuracy";
		
		chart = ChartFactory.createXYLineChart(chartTitle, 
				xAxisLabel, yAxisLabel, plotdata, PlotOrientation.VERTICAL, true, true, false );
		setLineChartOptions(chart,plotdata);
		
		return chart;
	}
	
	/**
	 * creates the JFreeChart panel to render the degree distribution chart
	 * @param plotdata
	 * @param title
	 * @return
	 */
	private JFreeChart createDegreeDistributionChart(XYDataset plotdata){
		
		JFreeChart chart = null;
		String chartTitle = "Scatter Plot showing Degree Distribution \n";
		String xAxisLabel = "Degree";
		String yAxisLabel = "Frequency";
		
		chart = ChartFactory.createScatterPlot(chartTitle, 
				xAxisLabel, yAxisLabel, plotdata, PlotOrientation.VERTICAL, true, true, false );
		setScatterChartOptions(chart);
		return chart;
	}
	
	/**
	 * support function to create the associated chart
	 * @param plotdata
	 * @param event
	 * @return
	 */
	private JPanel createChartPanel(XYDataset plotdata, int event){
				
		JFreeChart chart = null;
		File imageFile=null;
		String charttitle=null;
		
		//Survive
		if(event == 1){
			charttitle = "Survive Event";
			chart=createClassificationAccuracyChart(plotdata,charttitle);
			imageFile = new File("bin\\Result_Survive.png");
		}
		
		//Merge
		else if(event == 2){
			charttitle = "Merge Event";
			chart=createClassificationAccuracyChart(plotdata,charttitle);
			imageFile = new File("bin\\Result_Merge.png");
		}
		
		//Split
		else if(event == 3){
			charttitle = "Split Event";
			chart=createClassificationAccuracyChart(plotdata,charttitle);
			imageFile = new File("bin\\Result_Split.png");
		}
		
		//Dissolve
		else if(event == 4){
			charttitle = "Dissolve Event";
			chart=createClassificationAccuracyChart(plotdata,charttitle);
			imageFile = new File("bin\\Result_Dissolve.png");
		}
		
		//Multiclass
		else if(event == 5){
			charttitle = "MultiClass Event";
			chart=createClassificationAccuracyChart(plotdata,charttitle);
			imageFile = new File("bin\\Result_Multiclass.png");
		}
		
		//Degree Distribution
		else if(event == 0){
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
	
	/**
	 * set the line chart rendering options
	 * @param chart
	 * @param plotdata
	 */
	private void setLineChartOptions(JFreeChart chart, XYDataset plotdata) {
		XYPlot plot = chart.getXYPlot();
		SymbolAxis xAxisTicks = new SymbolAxis("Problem Class",new String[]{"","Intra-Features","Inter-Features","Selective-Features"});
		xAxisTicks.setLabelFont(new Font("SansSerif", Font.PLAIN, 20));
		xAxisTicks.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		xAxisTicks.setRange(0.9,3.1);
		plot.setDomainAxis(xAxisTicks);
		

		ValueAxis rangeAxis = plot.getRangeAxis();
		
		Number least = DatasetUtilities.findMinimumRangeValue(plotdata);
		Number largest = DatasetUtilities.findMaximumRangeValue(plotdata);

		rangeAxis.setRange(Math.max(0, (least.doubleValue()-5)), Math.min(100, (largest.doubleValue()+5)));
		rangeAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 20));

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
		
		// sets legend position
		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);
		legend.setItemFont(new Font("SansSerif", Font.PLAIN, 16));
	}
	
	/**
	 * set the scatter chart rendering options
	 * @param chart
	 */
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
		
		// sets legend position
		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);
		legend.setItemFont(new Font("SansSerif", Font.PLAIN, 16));
	}
	
	/**
	 * chart invoker
	 * @param dataset
	 * @param event
	 */
	public static void generateChart(final XYDataset dataset,int event){
		
        final int e = event;
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	new ChartVisualization(dataset, e).setVisible(false);
            }
        });
	}
	
}
