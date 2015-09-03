package main.sg.javapackage.weka.filters;

import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.logging.Logger;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Evaluation segregation of each problem class
 * for predicting each event
 * -Survive, Merge, Split, Dissolve
 * @author Stephen
 *
 */
public class EventBasedInstanceFilter {
	
	private static String resultFile = GlobalVariables.resultFile;
	public EventBasedInstanceFilter() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Selects the attributes based on the event being 
	 * modeled - survive, merge, split, dissolve
	 * @param data
	 * @param i - integer corresponding to event
	 * 			1-survive
	 * 			2-merge
	 * 			3-split
	 * 			4-dissove
	 * 
	 * @return updated instances
	 * @throws Exception
	 */
	public static Instances eventFilter(Instances data, int i) throws Exception{
		
		int n_attributes = data.numAttributes();
		String options=null;
		Remove remove =  new Remove();
		
		if(i == 1){
			Logger.writeToFile(resultFile,"\nFor SURVIVE :-\n",true);
			options = ""+(n_attributes-2)+","+(n_attributes-1)+","+(n_attributes);
			remove.setAttributeIndices(options);
		}
		else if(i == 2){	
			Logger.writeToFile(resultFile,"\nFor MERGE :-\n",true);
			options = ""+(n_attributes-3)+","+(n_attributes-1)+","+(n_attributes);
			remove.setAttributeIndices(options);
		}
		else if(i == 3){
			Logger.writeToFile(resultFile,"\nFor SPLIT :-\n",true);
			options = ""+(n_attributes-3)+","+(n_attributes-2)+","+(n_attributes);
			remove.setAttributeIndices(options);
		}
		else if(i == 4){
			Logger.writeToFile(resultFile,"\nFor DISSOLVE :-\n",true);
			options = ""+(n_attributes-3)+","+(n_attributes-2)+","+(n_attributes-1);
			remove.setAttributeIndices(options);
		}

		remove.setInputFormat(data);
		Instances updated_traningset = Filter.useFilter(data, remove);
		return updated_traningset;
	}

}
