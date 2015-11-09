package main.sg.javapackage.weka.filters;

import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.logging.Logger;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.SpreadSubsample;

/**
 * Support class for supervised modeling
 * SpreadSubSample filter from WEKA API
 * @author Stephen
 *
 */
public class SpreadSubsampleFilter {
	
	private static String resultFile = GlobalVariables.resultFile;
	public SpreadSubsampleFilter() {
		
	}
	
	/**
	 * applies SpreadSubsample filter to the  
	 * the training data
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static Instances applySpreadSubsample(Instances data) throws Exception{
		
		SpreadSubsample spreadsubsampler = new SpreadSubsample();
		spreadsubsampler.setDistributionSpread(1.0);
		spreadsubsampler.setInputFormat(data);
		Instances updated_traningset = Filter.useFilter(data, spreadsubsampler);
		Logger.writeToFile(resultFile,"Nominal Class - After SpreadSubSample\n",true);
		Logger.writeToFile(resultFile,updated_traningset.attributeStats(updated_traningset.numAttributes() - 1).toString()+"\n",true);
		return updated_traningset;
	}

}
