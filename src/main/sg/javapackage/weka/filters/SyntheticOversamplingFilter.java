package main.sg.javapackage.weka.filters;

import main.sg.javapackage.logging.Logger;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;

public class SyntheticOversamplingFilter {
	
	public SyntheticOversamplingFilter() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * applies SMOTE filter to the  
	 * the training data
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static Instances applySMOTE(Instances data) throws Exception{
		
		SMOTE smote = new SMOTE();
		smote.setClassValue("0");
		smote.setInputFormat(data);
		Instances updated_traningset = Filter.useFilter(data, smote);
		Logger.writeToLogln("Nominal Class - After SMOTE");
		Logger.writeToLogln(updated_traningset.attributeStats(updated_traningset.numAttributes() - 1).toString());
		return updated_traningset;
	}

}
