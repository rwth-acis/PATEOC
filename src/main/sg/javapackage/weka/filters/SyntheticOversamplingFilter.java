package main.sg.javapackage.weka.filters;

import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.logging.Logger;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;

/**
 * Support class for supervised modeling
 * SMOTE filter from WEKA API
 * @author Stephen
 *
 */
public class SyntheticOversamplingFilter {
	
	private static String resultFile = GlobalVariables.resultFile;
	public SyntheticOversamplingFilter() {
		
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
		Logger.writeToFile(resultFile,"Nominal Class - After SMOTE\n",true);
		Logger.writeToFile(resultFile,updated_traningset.attributeStats(updated_traningset.numAttributes() - 1).toString()+"\n",true);
		return updated_traningset;
	}

}
