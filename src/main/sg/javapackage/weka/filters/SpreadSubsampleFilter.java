package main.sg.javapackage.weka.filters;

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
	
	public SpreadSubsampleFilter() {
		// TODO Auto-generated constructor stub
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
		Logger.writeToLogln("Nominal Class - After SpreadSubSample");
		Logger.writeToLogln(updated_traningset.attributeStats(updated_traningset.numAttributes() - 1).toString());
		return updated_traningset;
	}

}
